package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.AdminBookingFilterRequest;
import com.phungvanlong.booking_hotel.dto.request.BookingExtraServiceDto;
import com.phungvanlong.booking_hotel.dto.request.BookingRequest;
import com.phungvanlong.booking_hotel.dto.response.*;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.*;
import com.phungvanlong.booking_hotel.service.BookingService;
import com.phungvanlong.booking_hotel.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final PaymentRepository paymentRepository;
    private final VillaRepository villaRepository;
    private final VillaTypeRepository villaTypeRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PromotionService promotionService;
    private final PromotionRepository promotionRepository;
    private final ExtraServiceRepository extraServiceRepository;
    private final BookingExtraServiceRepository bookingExtraServiceRepository;
    private final com.phungvanlong.booking_hotel.service.EmailService emailService;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        if (!request.getCheckInDate().isBefore(request.getCheckOutDate())) {
            throw new BusinessException("Ngày trả phòng/villa phải sau ngày nhận");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("Không tìm thấy user: " + userEmail));

        Long targetTypeId = request.getTargetVillaTypeId();
        Villa specificVilla = null;
        if (request.getVillaId() != null) {
            specificVilla = villaRepository.findById(request.getVillaId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy căn Villa được chọn"));
            if (targetTypeId == null && specificVilla.getVillaType() != null) {
                targetTypeId = specificVilla.getVillaType().getId();
            }
        }

        if (targetTypeId == null && specificVilla == null) {
            throw new BusinessException("Vui lòng chọn hạng Villa cần đặt");
        }

        VillaType villaType = targetTypeId != null ? villaTypeRepository.findById(targetTypeId).orElse(null) : null;
        int quantity = request.getQuantity() != null && request.getQuantity() > 0 ? request.getQuantity() : 1;
        long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (days <= 0)
            days = 1;

        BigDecimal dailyPrice;
        List<Villa> selectedVillas = new ArrayList<>();
        List<Room> selectedRooms = new ArrayList<>();

        if (specificVilla != null) {
            long overlaps = bookingRepository.countOverlappingBookingsForVilla(
                    specificVilla.getId(), request.getCheckInDate(), request.getCheckOutDate());
            if (overlaps > 0) {
                throw new BusinessException("Căn " + specificVilla.getVillaNumber() + " (" + (specificVilla.getVillaType() != null ? specificVilla.getVillaType().getName() : "") + ") đã có khách đặt trong khoảng thời gian " + request.getCheckInDate() + " - " + request.getCheckOutDate() + ". Vui lòng chọn căn khác hoặc đổi ngày!");
            }
            selectedVillas.add(specificVilla);

            // Áp dụng Dynamic Pricing nếu hạng Villa đang kích hoạt định giá linh hoạt
            VillaType vt = specificVilla.getVillaType();
            if (vt != null && Boolean.TRUE.equals(vt.getIsDynamicPricingEnabled()) && vt.getDynamicPrice() != null && vt.getDynamicPrice().compareTo(BigDecimal.ZERO) > 0) {
                dailyPrice = vt.getDynamicPrice();
            } else if (specificVilla.getBasePrice() != null && specificVilla.getBasePrice().compareTo(BigDecimal.ZERO) > 0) {
                dailyPrice = specificVilla.getBasePrice();
            } else if (vt != null && vt.getBasePrice() != null) {
                dailyPrice = vt.getBasePrice();
            } else {
                dailyPrice = BigDecimal.ZERO;
            }
        } else if (villaType != null) {
            if (Boolean.TRUE.equals(villaType.getIsDynamicPricingEnabled()) && villaType.getDynamicPrice() != null && villaType.getDynamicPrice().compareTo(BigDecimal.ZERO) > 0) {
                dailyPrice = villaType.getDynamicPrice();
            } else {
                dailyPrice = villaType.getBasePrice();
            }

            List<Villa> availableVillas = bookingRepository.findAvailableVillas(
                    villaType.getId(), request.getCheckInDate(), request.getCheckOutDate());

            if (availableVillas.size() < quantity) {
                throw new BusinessException("Không đủ Villa trống trong khoảng thời gian này (chỉ còn " + availableVillas.size() + " căn trống)");
            }
            selectedVillas = availableVillas.subList(0, quantity);
        } else {
            // Backward compatibility: Fallback if old roomType requested
            RoomType roomType = roomTypeRepository.findById(targetTypeId)
                    .orElseThrow(() -> new BusinessException("Không tìm thấy hạng Villa/phòng"));
            if (Boolean.TRUE.equals(roomType.getIsDynamicPricingEnabled()) && roomType.getDynamicPrice() != null && roomType.getDynamicPrice().compareTo(BigDecimal.ZERO) > 0) {
                dailyPrice = roomType.getDynamicPrice();
            } else {
                dailyPrice = roomType.getBasePrice();
            }

            List<Room> availableRooms = bookingRepository.findAvailableRooms(
                    roomType.getId(), request.getCheckInDate(), request.getCheckOutDate());
            if (availableRooms.size() < quantity) {
                throw new BusinessException("Không đủ phòng/villa trống trong khoảng thời gian này");
            }
            selectedRooms = availableRooms.subList(0, quantity);
        }

        BigDecimal totalAmount = dailyPrice
                .multiply(BigDecimal.valueOf(days))
                .multiply(BigDecimal.valueOf(quantity));

        // Xử lý Extra Services
        List<BookingExtraService> bookingExtraServices = new ArrayList<>();
        if (request.getExtraServices() != null && !request.getExtraServices().isEmpty()) {
            for (BookingExtraServiceDto dto : request.getExtraServices()) {
                ExtraService service = extraServiceRepository.findById(dto.getServiceId())
                        .orElseThrow(() -> new BusinessException("Không tìm thấy dịch vụ: " + dto.getServiceId()));

                BookingExtraService bes = BookingExtraService.builder()
                        .extraService(service)
                        .quantity(dto.getQuantity())
                        .priceAtBooking(service.getPrice())
                        .build();
                bookingExtraServices.add(bes);

                BigDecimal serviceCost = service.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
                totalAmount = totalAmount.add(serviceCost);
            }
        }

        Promotion promotion = null;
        if (request.getPromotionCode() != null && !request.getPromotionCode().isEmpty()) {
            promotion = promotionService.validatePromotionCode(request.getPromotionCode());

            if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
                BigDecimal discount = totalAmount.multiply(promotion.getDiscountValue())
                        .divide(BigDecimal.valueOf(100));
                totalAmount = totalAmount.subtract(discount);
            } else if (promotion.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                totalAmount = totalAmount.subtract(promotion.getDiscountValue());
            }

            if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                totalAmount = BigDecimal.ZERO;
            }

            if (promotion.getQuantity() != null) {
                promotion.setQuantity(promotion.getQuantity() - 1);
                promotionRepository.save(promotion);
            }
        }

        String bookingCode = "BK-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        boolean isPayAtHotel = "pay_at_hotel".equals(request.getPaymentMethod());

        String guestName = (request.getGuestName() != null && !request.getGuestName().isBlank())
                ? request.getGuestName().trim()
                : (user.getFullName() != null && !user.getFullName().isBlank() ? user.getFullName() : "Quý khách");

        String guestEmail = (request.getGuestEmail() != null && !request.getGuestEmail().isBlank())
                ? request.getGuestEmail().trim()
                : user.getEmail();

        String guestPhone = (request.getGuestPhone() != null && !request.getGuestPhone().isBlank())
                ? request.getGuestPhone().trim()
                : user.getPhone();

        String bookingNote = request.getNote();
        if ((bookingNote == null || bookingNote.isBlank()) && request.getSpecialRequest() != null) {
            bookingNote = request.getSpecialRequest();
        }

        Booking booking = Booking.builder()
                .bookingCode(bookingCode)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalAmount(totalAmount)
                // pay_at_hotel -> CONFIRMED ngay, không cần đợi thanh toán online
                .status(isPayAtHotel ? BookingStatus.CONFIRMED : BookingStatus.PENDING)
                .expireAt(isPayAtHotel ? null : LocalDateTime.now().plusMinutes(15))
                .note(bookingNote)
                .guestName(guestName)
                .guestEmail(guestEmail)
                .guestPhone(guestPhone)
                .user(user)
                .promotion(promotion)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        List<BookingDetail> details = new ArrayList<>();
        for (Villa v : selectedVillas) {
            details.add(BookingDetail.builder()
                    .booking(savedBooking)
                    .villa(v)
                    .pricePerNight(dailyPrice)
                    .build());
        }
        for (Room r : selectedRooms) {
            details.add(BookingDetail.builder()
                    .booking(savedBooking)
                    .room(r)
                    .pricePerNight(dailyPrice)
                    .build());
        }

        if (!details.isEmpty()) {
            bookingDetailRepository.saveAll(details);
            savedBooking.setBookingDetails(details);
        }

        if (!bookingExtraServices.isEmpty()) {
            bookingExtraServices.forEach(bes -> bes.setBooking(savedBooking));
            bookingExtraServiceRepository.saveAll(bookingExtraServices);
            savedBooking.setExtraServices(bookingExtraServices);
        }

        Payment payment = Payment.builder()
                .booking(savedBooking)
                .amount(totalAmount)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        if (isPayAtHotel) {
            final String recipientEmail = guestEmail;
            final String recipientName = guestName;
            final LocalDate checkIn = savedBooking.getCheckInDate();
            final LocalDate checkOut = savedBooking.getCheckOutDate();
            final BigDecimal amount = savedBooking.getTotalAmount();

            if (recipientEmail != null && !recipientEmail.isBlank()) {
                java.util.concurrent.CompletableFuture.runAsync(() -> {
                    try {
                        log.info("Bắt đầu gửi email xác nhận đặt phòng tới: {}", recipientEmail);
                        emailService.sendBookingSuccessEmail(recipientEmail, bookingCode, recipientName, checkIn, checkOut, amount);
                        log.info("Đã gửi email xác nhận đặt phòng thành công tới: {}", recipientEmail);
                    } catch (Exception e) {
                        log.error("Lỗi khi gửi email xác nhận đặt phòng tới {}: {}", recipientEmail, e.getMessage(), e);
                    }
                });
            } else {
                log.warn("Không tìm thấy email người nhận để gửi xác nhận cho đơn {}", bookingCode);
            }
        }

        return BookingResponse.fromEntity(savedBooking);
    }

    @Override
    public BookingResponse getBookingById(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new BusinessException("Không tìm thấy user: " + userEmail));
            if (user.getRole() != Role.ROLE_ADMIN && user.getRole() != Role.ROLE_STAFF) {
                throw new BusinessException("Không có quyền truy cập đơn này");
            }
        }
        return BookingResponse.fromEntity(booking);
    }

    @Override
    public List<BookingResponse> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("Không tìm thấy user"));

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());
        return bookings.stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("Không tìm thấy user: " + userEmail));
        if (!booking.getUser().getId().equals(user.getId()) &&
                user.getRole() != Role.ROLE_ADMIN &&
                user.getRole() != Role.ROLE_STAFF) {
            throw new BusinessException("Không có quyền hủy đơn này");
        }

        if (booking.getStatus() == BookingStatus.CHECKED_IN || booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new BusinessException("Không thể hủy đơn đã check-in hoặc check-out");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Đơn đặt phòng này đã được hủy trước đó");
        }

        // Chính sách hủy: Khách tự hủy phải trước ngày nhận phòng. Nếu hủy sát ngày, yêu cầu liên hệ Lễ tân.
        if (user.getRole() != Role.ROLE_ADMIN && user.getRole() != Role.ROLE_STAFF) {
            if (booking.getCheckInDate() != null) {
                LocalDate today = LocalDate.now();
                if (!today.isBefore(booking.getCheckInDate())) {
                    throw new BusinessException("Đã đến hoặc cận ngày nhận phòng. Vui lòng liên hệ trực tiếp Lễ tân qua hotline 0901 234 567 để được hỗ trợ hủy.");
                }
            }
        }

        // Nếu đơn đã thanh toán trực tuyến thành công, lưu ghi chú đối soát hoàn tiền cho kế toán
        Payment payment = booking.getPayment();
        if (payment != null && payment.getStatus() == PaymentStatus.SUCCESS) {
            payment.setReconciliationNote("Khách hủy đơn ngày " + LocalDate.now() + " - Chờ kế toán kiểm tra hoàn tiền");
            paymentRepository.save(payment);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        restorePromotion(booking);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public BookingResponse mockPaymentSuccess(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Đơn không ở trạng thái chờ thanh toán");
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        Payment payment = booking.getPayment();
        if (payment != null) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaymentMethod("MOCK_VNPAY");
            payment.setPaymentTime(LocalDateTime.now());
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8));
        }

        Booking updated = bookingRepository.save(booking);
        return BookingResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void cancelExpiredBookings() {
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpireAtBefore(BookingStatus.PENDING,
                LocalDateTime.now());
        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.CANCELLED);
            restorePromotion(booking);
        }
        if (!expiredBookings.isEmpty()) {
            bookingRepository.saveAll(expiredBookings);
        }
    }



    private void restorePromotion(Booking booking) {
        if (booking.getPromotion() != null && booking.getPromotion().getQuantity() != null) {
            Promotion promotion = booking.getPromotion();
            promotion.setQuantity(promotion.getQuantity() + 1);
            promotionRepository.save(promotion);
        }
    }
}
