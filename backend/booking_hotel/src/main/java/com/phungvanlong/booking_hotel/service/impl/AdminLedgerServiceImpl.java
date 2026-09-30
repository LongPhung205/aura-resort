package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.DayEndClosingRequest;
import com.phungvanlong.booking_hotel.dto.request.LedgerItemRequest;
import com.phungvanlong.booking_hotel.dto.response.DayEndClosingResponse;
import com.phungvanlong.booking_hotel.dto.response.LedgerItemResponse;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.repository.*;
import com.phungvanlong.booking_hotel.service.AdminLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminLedgerServiceImpl implements AdminLedgerService {

    private final PaymentRepository paymentRepository;
    private final DayEndClosingRepository dayEndClosingRepository;
    private final BookingRepository bookingRepository;
    private final VillaRepository villaRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    public List<LedgerItemResponse> getLedgerTransactions(String ledgerType, String method) {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .filter(p -> ledgerType == null || ledgerType.isBlank() || ledgerType.equalsIgnoreCase("ALL") || ledgerType.equalsIgnoreCase(p.getLedgerType()))
                .filter(p -> method == null || method.isBlank() || method.equalsIgnoreCase("ALL") || method.equalsIgnoreCase(p.getPaymentMethod()))
                .map(LedgerItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LedgerItemResponse createTransaction(LedgerItemRequest request) {
        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CASH")
                .ledgerType(request.getLedgerType() != null ? request.getLedgerType() : "ROOM_CHARGE")
                .referenceNo(request.getReferenceNo() != null ? request.getReferenceNo() : "TXN-" + System.currentTimeMillis())
                .guestName(request.getGuestName() != null ? request.getGuestName() : "Khách vãng lai")
                .status(PaymentStatus.SUCCESS)
                .paymentTime(LocalDateTime.now())
                .transactionId("MANUAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();
        payment = paymentRepository.save(payment);
        return LedgerItemResponse.fromEntity(payment);
    }

    @Override
    public DayEndClosingResponse getLatestClosing() {
        return dayEndClosingRepository.findTopByOrderByClosingDateDesc()
                .map(DayEndClosingResponse::fromEntity)
                .orElseGet(() -> calculateLiveClosing(LocalDate.now(), null));
    }

    @Override
    @Transactional
    public DayEndClosingResponse executeDayEndClosing(DayEndClosingRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        return calculateLiveClosing(request.getClosingDate(), user);
    }

    private DayEndClosingResponse calculateLiveClosing(LocalDate date, User user) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getPaymentTime() != null && !p.getPaymentTime().isBefore(start) && !p.getPaymentTime().isAfter(end))
                .collect(Collectors.toList());

        BigDecimal totalRoomRev = BigDecimal.ZERO;
        BigDecimal totalServiceRev = BigDecimal.ZERO;

        for (Payment p : payments) {
            if (p.getStatus() == PaymentStatus.SUCCESS) {
                if ("EXTRA_SERVICE".equalsIgnoreCase(p.getLedgerType())) {
                    totalServiceRev = totalServiceRev.add(p.getAmount());
                } else {
                    totalRoomRev = totalRoomRev.add(p.getAmount());
                }
            }
        }

        BigDecimal totalRevenue = totalRoomRev.add(totalServiceRev);
        // Ước tính chi phí vận hành chuẩn 28% doanh thu
        BigDecimal totalOpex = totalRevenue.multiply(BigDecimal.valueOf(0.28));
        BigDecimal netCash = totalRevenue.subtract(totalOpex);

        long totalVillas = villaRepository.count();
        if (totalVillas == 0) totalVillas = roomRepository.count();
        long occupiedVillas = villaRepository.findAll().stream().filter(v -> v.getStatus() == VillaStatus.OCCUPIED).count();
        if (occupiedVillas == 0 && totalVillas == roomRepository.count()) {
            occupiedVillas = roomRepository.findAll().stream().filter(r -> r.getStatus() == RoomStatus.OCCUPIED).count();
        }
        double occupancy = totalVillas > 0 ? ((double) occupiedVillas / totalVillas) * 100.0 : 0.0;

        DayEndClosing closing = dayEndClosingRepository.findByClosingDate(date)
                .orElse(DayEndClosing.builder().closingDate(date).build());

        closing.setTotalRevenue(totalRevenue);
        closing.setRoomRevenue(totalRoomRev);
        closing.setServiceRevenue(totalServiceRev);
        closing.setTotalOpex(totalOpex);
        closing.setNetCash(netCash);
        closing.setOccupancyRate(occupancy);
        closing.setTotalBookings((int) bookingRepository.count());
        closing.setOccupiedRooms((int) occupiedVillas);
        closing.setClosedBy(user);

        if (user != null) {
            closing.setStatus("LOCKED");
            closing.setClosedAt(LocalDateTime.now());
            closing.setNotes("Chốt sổ kế toán tự động lúc " + LocalDateTime.now().toString());
            closing = dayEndClosingRepository.save(closing);
        }

        return DayEndClosingResponse.fromEntity(closing);
    }
}
