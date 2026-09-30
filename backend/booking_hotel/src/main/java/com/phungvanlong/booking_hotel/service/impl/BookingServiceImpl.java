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

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        if (!request.getCheckInDate().isBefore(request.getCheckOutDate())) {
            throw new BusinessException("Ngày trả phòng/villa phải sau ngày nhận");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("Không tìm thấy user: " + userEmail));

        Long targetTypeId = request.getTargetVillaTypeId();
        if (targetTypeId == null) {
            throw new BusinessException("Vui lòng chọn hạng Villa cần đặt");
        }

        VillaType villaType = villaTypeRepository.findById(targetTypeId).orElse(null);
        int quantity = request.getQuantity() != null && request.getQuantity() > 0 ? request.getQuantity() : 1;
        long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (days <= 0) days = 1;

        BigDecimal basePrice;
        List<Villa> selectedVillas = new ArrayList<>();

        if (villaType != null) {
            basePrice = villaType.getBasePrice();
            List<Villa> availableVillas = bookingRepository.findAvailableVillas(
                    villaType.getId(), request.getCheckInDate(), request.getCheckOutDate());

            if (availableVillas.size() < quantity) {
                throw new BusinessException("Không đủ Villa trống trong khoảng thời gian này");
            }
            selectedVillas = availableVillas.subList(0, quantity);
        } else {
            // Backward compatibility: Fallback if old roomType requested
            RoomType roomType = roomTypeRepository.findById(targetTypeId)
                    .orElseThrow(() -> new BusinessException("Không tìm thấy hạng Villa/phòng"));
            basePrice = roomType.getBasePrice();
            List<Room> availableRooms = bookingRepository.findAvailableRooms(
                    roomType.getId(), request.getCheckInDate(), request.getCheckOutDate());
            if (availableRooms.size() < quantity) {
                throw new BusinessException("Không đủ phòng/villa trống trong khoảng thời gian này");
            }
        }

        BigDecimal totalAmount = basePrice
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
                BigDecimal discount = totalAmount.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100));
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

        Booking booking = Booking.builder()
                .bookingCode(bookingCode)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING)
                .expireAt(LocalDateTime.now().plusMinutes(15))
                .note(request.getNote())
                .user(user)
                .promotion(promotion)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        List<BookingDetail> details = new ArrayList<>();
        for (Villa v : selectedVillas) {
            details.add(BookingDetail.builder()
                    .booking(savedBooking)
                    .villa(v)
                    .pricePerNight(basePrice)
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

        return BookingResponse.fromEntity(savedBooking);
    }

    @Override
    public BookingResponse getBookingById(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng"));
        
        if (!booking.getUser().getEmail().equals(userEmail)) {
            User user = userRepository.findByEmail(userEmail).get();
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

        User user = userRepository.findByEmail(userEmail).get();
        if (!booking.getUser().getId().equals(user.getId()) && 
            user.getRole() != Role.ROLE_ADMIN && 
            user.getRole() != Role.ROLE_STAFF) {
            throw new BusinessException("Không có quyền hủy đơn này");
        }

        if (booking.getStatus() == BookingStatus.CHECKED_IN || booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new BusinessException("Không thể hủy đơn đã check-in hoặc check-out");
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
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0,8));
        }

        Booking updated = bookingRepository.save(booking);
        return BookingResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void cancelExpiredBookings() {
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpireAtBefore(BookingStatus.PENDING, LocalDateTime.now());
        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.CANCELLED);
            restorePromotion(booking);
        }
        if (!expiredBookings.isEmpty()) {
            bookingRepository.saveAll(expiredBookings);
        }
    }

    @Override
    @Transactional
    public BookingResponse checkInBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessException("Chỉ đơn ở trạng thái CONFIRMED mới được Check-in");
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        if (booking.getBookingDetails() != null) {
            for (BookingDetail bd : booking.getBookingDetails()) {
                if (bd.getVilla() != null) {
                    bd.getVilla().setStatus(VillaStatus.OCCUPIED);
                    bd.getVilla().setCurrentGuestName(booking.getUser() != null ? booking.getUser().getFullName() : "Khách lưu trú");
                    villaRepository.save(bd.getVilla());
                }
            }
        }
        return BookingResponse.fromEntity(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse checkOutBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng"));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new BusinessException("Chỉ đơn ở trạng thái CHECKED_IN mới được Check-out");
        }

        booking.setStatus(BookingStatus.CHECKED_OUT);
        if (booking.getBookingDetails() != null) {
            for (BookingDetail bd : booking.getBookingDetails()) {
                if (bd.getVilla() != null) {
                    bd.getVilla().setStatus(VillaStatus.CLEANING);
                    bd.getVilla().setCurrentGuestName(null);
                    villaRepository.save(bd.getVilla());
                }
            }
        }
        return BookingResponse.fromEntity(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminBookingItemResponse> getAdminBookings(AdminBookingFilterRequest filterRequest) {
        int pageNo = Math.max(0, filterRequest.getPage());
        int pageSize = filterRequest.getSize() > 0 ? filterRequest.getSize() : 10;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        BookingStatus statusEnum = null;
        if (filterRequest.getStatus() != null && !filterRequest.getStatus().isBlank() && !"ALL".equalsIgnoreCase(filterRequest.getStatus())) {
            try {
                statusEnum = BookingStatus.valueOf(filterRequest.getStatus().toUpperCase());
            } catch (Exception ignored) {}
        }

        Page<Booking> pageResult = bookingRepository.searchAdminBookings(
                filterRequest.getSearch(),
                statusEnum,
                filterRequest.getFromDate(),
                filterRequest.getToDate(),
                pageable);

        List<AdminBookingItemResponse> items = new ArrayList<>();

        if (pageResult.hasContent()) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM");

            for (Booking b : pageResult.getContent()) {
                String villaNum = "Chưa gán";
                String typeName = "Biệt thự cao cấp";
                if (b.getBookingDetails() != null && !b.getBookingDetails().isEmpty()) {
                    var detail = b.getBookingDetails().get(0);
                    if (detail.getVilla() != null) {
                        villaNum = detail.getVilla().getVillaNumber();
                        if (detail.getVilla().getVillaType() != null) {
                            typeName = detail.getVilla().getVillaType().getName();
                        }
                    } else if (detail.getRoom() != null) {
                        villaNum = "Villa #" + detail.getRoom().getRoomNumber();
                        if (detail.getRoom().getRoomType() != null) {
                            typeName = detail.getRoom().getRoomType().getName();
                        }
                    }
                }

                int nights = (int) ChronoUnit.DAYS.between(b.getCheckInDate(), b.getCheckOutDate());
                if (nights <= 0) nights = 1;

                items.add(AdminBookingItemResponse.builder()
                        .id(b.getId())
                        .bookingCode(b.getBookingCode())
                        .bookingDateFormatted(b.getCreatedAt() != null ? b.getCreatedAt().format(dtf) : "")
                        .guestName(b.getGuestName() != null && !b.getGuestName().isBlank() ? b.getGuestName() : (b.getUser() != null ? b.getUser().getFullName() : "Khách hàng Aura"))
                        .avatarUrl(null)
                        .guestCountry("Việt Nam")
                        .guestPhone(b.getGuestPhone() != null && !b.getGuestPhone().isBlank() ? b.getGuestPhone() : (b.getUser() != null ? b.getUser().getPhone() : ""))
                        .guestTier("Thành viên")
                        .tierBadgeColor("bg-slate-100 text-slate-800 border-slate-300")
                        .villaNumber(villaNum)
                        .villaTypeName(typeName)
                        .roomTypeName(typeName)
                        .checkInFormatted(b.getCheckInDate() != null ? b.getCheckInDate().format(dateFmt) : "")
                        .checkOutFormatted(b.getCheckOutDate() != null ? b.getCheckOutDate().format(dateFmt) : "")
                        .nights(nights)
                        .guestSummary(nights + " đêm")
                        .channel("Hệ Thống Trực Tiếp")
                        .channelBadgeColor("bg-emerald-50 text-emerald-700")
                        .totalAmount(b.getTotalAmount())
                        .totalAmountDisplay(b.getTotalAmount() != null ? String.format("%,.0f₫", b.getTotalAmount()) : "0₫")
                        .paymentStatusDisplay(b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.CHECKED_IN || b.getStatus() == BookingStatus.CHECKED_OUT ? "Đã thanh toán" : "Chờ thanh toán")
                        .isFullyPaid(b.getStatus() != BookingStatus.PENDING)
                        .extraServiceName(null)
                        .extraServiceIcon(null)
                        .assignedButler(null)
                        .statusCode(b.getStatus().name())
                        .statusLabel(b.getStatus() == BookingStatus.CHECKED_IN ? "Đang Lưu Trú" :
                                     b.getStatus() == BookingStatus.CONFIRMED ? "Đã Xác Nhận" :
                                     b.getStatus() == BookingStatus.PENDING ? "Chờ Thanh Toán" : "Đã Trả Phòng")
                        .statusBadgeColor(b.getStatus() == BookingStatus.CHECKED_IN ? "bg-sky-50 text-sky-700 border-sky-300" :
                                          b.getStatus() == BookingStatus.CONFIRMED ? "bg-emerald-50 text-emerald-700 border-emerald-300" :
                                          "bg-amber-50 text-amber-700 border-amber-300")
                        .build());
            }
        }

        return PageResponse.<AdminBookingItemResponse>builder()
                .content(items)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GanttVillaAvailabilityResponse> getGanttVillaAvailability(LocalDate startDate, int days) {
        final LocalDate effStartDate = (startDate != null) ? startDate : LocalDate.now();
        final int effDays = (days <= 0) ? 7 : days;

        List<Villa> villas = villaRepository.findAll();
        if (villas.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<GanttVillaAvailabilityResponse> rows = new ArrayList<>();
        LocalDate endDate = effStartDate.plusDays(effDays);

        // Lấy tất cả active bookings trong khoảng thời gian này
        List<Booking> activeBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .filter(b -> b.getCheckInDate() != null && b.getCheckOutDate() != null)
                .filter(b -> !b.getCheckInDate().isAfter(endDate) && !b.getCheckOutDate().isBefore(effStartDate))
                .collect(Collectors.toList());

        for (Villa villa : villas) {
            List<GanttVillaAvailabilityResponse.GanttDaySlot> slots = new ArrayList<>();
            String villaName = villa.getVillaNumber();
            String villaTypeName = villa.getVillaType() != null ? villa.getVillaType().getName() : "Villa Resort";
            String zoneName = villa.getZone() != null ? villa.getZone().getName() : "Khu Biệt Thự";

            // Lọc các booking gắn với villa này
            List<Booking> villaBookings = activeBookings.stream()
                    .filter(b -> b.getBookingDetails() != null && b.getBookingDetails().stream()
                            .anyMatch(bd -> bd.getVilla() != null && bd.getVilla().getId().equals(villa.getId())))
                    .collect(Collectors.toList());

            for (int i = 0; i < effDays; i++) {
                LocalDate currentDate = effStartDate.plusDays(i);
                String dateLabel = String.format("%02d/%02d", currentDate.getDayOfMonth(), currentDate.getMonthValue());

                // Tìm booking cho ngày hiện tại
                Booking matchBooking = villaBookings.stream()
                        .filter(b -> !currentDate.isBefore(b.getCheckInDate()) && currentDate.isBefore(b.getCheckOutDate()))
                        .findFirst()
                        .orElse(null);

                if (matchBooking != null) {
                    boolean isStart = currentDate.equals(matchBooking.getCheckInDate());
                    int span = (int) ChronoUnit.DAYS.between(matchBooking.getCheckInDate(), matchBooking.getCheckOutDate());
                    String guestName = matchBooking.getUser() != null ? matchBooking.getUser().getFullName() : "Khách lưu trú";
                    String status = matchBooking.getStatus() == BookingStatus.CHECKED_IN ? "OCCUPIED" : "CONFIRMED";
                    String colorClass = matchBooking.getStatus() == BookingStatus.CHECKED_IN ? "bg-sky-600 text-white" : "bg-amber-600 text-white";

                    slots.add(new GanttVillaAvailabilityResponse.GanttDaySlot(
                            dateLabel, status, guestName, span, isStart,
                            isStart ? guestName + " (" + span + " Đêm)" : "",
                            colorClass
                    ));
                } else {
                    String status = "AVAILABLE";
                    String colorClass = "bg-slate-100 text-slate-500";
                    if (currentDate.equals(LocalDate.now())) {
                        if (villa.getStatus() == VillaStatus.CLEANING) {
                            status = "CLEANING";
                            colorClass = "bg-amber-100 text-amber-900 border border-amber-300";
                        } else if (villa.getStatus() == VillaStatus.MAINTENANCE) {
                            status = "MAINTENANCE";
                            colorClass = "bg-rose-100 text-rose-700 border border-rose-300";
                        }
                    }
                    slots.add(new GanttVillaAvailabilityResponse.GanttDaySlot(
                            dateLabel, status, null, 1, true, "Trống", colorClass
                    ));
                }
            }
            rows.add(new GanttVillaAvailabilityResponse(villaName, villaTypeName, zoneName, slots));
        }

        return rows;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GanttRoomAvailabilityResponse> getGanttAvailability(LocalDate startDate, int days) {
        List<GanttVillaAvailabilityResponse> villas = getGanttVillaAvailability(startDate, days);
        return villas.stream().map(v -> {
            List<GanttRoomAvailabilityResponse.GanttDaySlot> slots = v.getDaySlots().stream()
                    .map(s -> new GanttRoomAvailabilityResponse.GanttDaySlot(
                            s.getDateLabel(), s.getStatus(), s.getGuestName(),
                            s.getSpanDays(), s.getIsSpanStart(), s.getBlockLabel(), s.getColorClass()))
                    .collect(Collectors.toList());
            return new GanttRoomAvailabilityResponse(v.getVillaNumber(), v.getVillaTypeName(), slots);
        }).collect(Collectors.toList());
    }

    private void restorePromotion(Booking booking) {
        if (booking.getPromotion() != null && booking.getPromotion().getQuantity() != null) {
            Promotion promotion = booking.getPromotion();
            promotion.setQuantity(promotion.getQuantity() + 1);
            promotionRepository.save(promotion);
        }
    }
}
