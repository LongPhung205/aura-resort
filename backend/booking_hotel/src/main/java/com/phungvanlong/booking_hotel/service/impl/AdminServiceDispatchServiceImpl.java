package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.ServiceDispatchRequest;
import com.phungvanlong.booking_hotel.dto.response.ServiceDispatchResponse;
import com.phungvanlong.booking_hotel.entity.Booking;
import com.phungvanlong.booking_hotel.entity.ServiceDispatch;
import com.phungvanlong.booking_hotel.entity.User;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.repository.BookingRepository;
import com.phungvanlong.booking_hotel.repository.ServiceDispatchRepository;
import com.phungvanlong.booking_hotel.repository.UserRepository;
import com.phungvanlong.booking_hotel.service.AdminServiceDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceDispatchServiceImpl implements AdminServiceDispatchService {

    private final ServiceDispatchRepository serviceDispatchRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public List<ServiceDispatchResponse> getAllDispatches(String status) {
        List<ServiceDispatch> list;
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            list = serviceDispatchRepository.findByStatusOrderByScheduledTimeAsc(status);
        } else {
            list = serviceDispatchRepository.findAllByOrderByScheduledTimeDesc();
        }
        return list.stream().map(ServiceDispatchResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceDispatchResponse createDispatch(ServiceDispatchRequest request) {
        Booking booking = null;
        if (request.getBookingId() != null) {
            booking = bookingRepository.findById(request.getBookingId()).orElse(null);
        }

        User staff = null;
        if (request.getStaffId() != null) {
            staff = userRepository.findById(request.getStaffId()).orElse(null);
        }

        ServiceDispatch dispatch = ServiceDispatch.builder()
                .booking(booking)
                .serviceType(request.getServiceType())
                .assetCode(request.getAssetCode())
                .guestName(request.getGuestName() != null ? request.getGuestName() : (booking != null ? booking.getGuestName() : "Khách VIP"))
                .roomNumber(request.getRoomNumber())
                .assignedStaff(staff)
                .pickupLocation(request.getPickupLocation())
                .destination(request.getDestination())
                .scheduledTime(request.getScheduledTime())
                .flightNumber(request.getFlightNumber())
                .cost(request.getCost() != null ? request.getCost() : BigDecimal.ZERO)
                .notes(request.getNotes())
                .status("DISPATCHED")
                .build();

        return ServiceDispatchResponse.fromEntity(serviceDispatchRepository.save(dispatch));
    }

    @Override
    @Transactional
    public ServiceDispatchResponse updateStatus(Long id, String status) {
        ServiceDispatch dispatch = serviceDispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lệnh điều phối ID: " + id));

        dispatch.setStatus(status);

        // Khi hoàn thành, nếu gắn với booking, tự động kết chuyển phí dịch vụ vào hóa đơn Folio
        if ("COMPLETED".equalsIgnoreCase(status) && dispatch.getBooking() != null && dispatch.getCost() != null) {
            Booking booking = dispatch.getBooking();
            BigDecimal currentFolio = booking.getFolioBalance() != null ? booking.getFolioBalance() : BigDecimal.ZERO;
            booking.setFolioBalance(currentFolio.add(dispatch.getCost()));
            booking.setTotalAmount(booking.getTotalAmount().add(dispatch.getCost()));
            bookingRepository.save(booking);
        }

        return ServiceDispatchResponse.fromEntity(serviceDispatchRepository.save(dispatch));
    }
}
