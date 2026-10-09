package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.AdminBookingFilterRequest;
import com.phungvanlong.booking_hotel.dto.response.*;
import com.phungvanlong.booking_hotel.entity.Booking;
import com.phungvanlong.booking_hotel.entity.BookingDetail;
import com.phungvanlong.booking_hotel.entity.BookingStatus;
import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import com.phungvanlong.booking_hotel.entity.Room;
import com.phungvanlong.booking_hotel.entity.HousekeepingTask;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.entity.User;
import com.phungvanlong.booking_hotel.entity.Payment;
import com.phungvanlong.booking_hotel.entity.PaymentStatus;
import com.phungvanlong.booking_hotel.repository.BookingRepository;
import com.phungvanlong.booking_hotel.repository.VillaRepository;
import com.phungvanlong.booking_hotel.repository.HousekeepingTaskRepository;
import com.phungvanlong.booking_hotel.repository.BookingDetailRepository;
import com.phungvanlong.booking_hotel.repository.PaymentRepository;
import com.phungvanlong.booking_hotel.repository.UserRepository;
import com.phungvanlong.booking_hotel.service.AdminBookingService;
import com.phungvanlong.booking_hotel.service.NotificationService;
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
public class AdminBookingServiceImpl implements AdminBookingService {

    private final BookingRepository bookingRepository;
    private final VillaRepository villaRepository;
    private final HousekeepingTaskRepository housekeepingTaskRepository;
    private final NotificationService notificationService;
    private final BookingDetailRepository bookingDetailRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final com.phungvanlong.booking_hotel.repository.RefillTaskRepository refillTaskRepository;

    @Override
    @Transactional
    public BookingResponse checkInBooking(Long id) {
        return checkInBooking(id, null);
    }

    @Override
    @Transactional
    public BookingResponse checkInBooking(Long id, String verifyCode) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessException("Chỉ đơn ở trạng thái CONFIRMED mới được Check-in");
        }

        if (verifyCode != null && !verifyCode.trim().isEmpty()) {
            String input = verifyCode.trim();
            String cleanInput = input.replaceAll("[\\s\\-\\.]", "");

            String bookingCode = booking.getBookingCode() != null ? booking.getBookingCode().trim() : "";
            String cleanBookingCode = bookingCode.replaceAll("[\\s\\-\\.]", "");

            String phone = booking.getGuestPhone() != null && !booking.getGuestPhone().isBlank()
                    ? booking.getGuestPhone().trim()
                    : (booking.getUser() != null && booking.getUser().getPhone() != null ? booking.getUser().getPhone().trim() : "");
            String cleanPhone = phone.replaceAll("[^0-9]", "");
            if (cleanPhone.startsWith("84")) {
                cleanPhone = "0" + cleanPhone.substring(2);
            }
            String cleanInputDigits = cleanInput.replaceAll("[^0-9]", "");
            if (cleanInputDigits.startsWith("84")) {
                cleanInputDigits = "0" + cleanInputDigits.substring(2);
            }

            boolean matched = false;

            // 1. So khớp mã Check-in đầy đủ
            if (cleanBookingCode.equalsIgnoreCase(cleanInput) || bookingCode.equalsIgnoreCase(input)) {
                matched = true;
            }

            // 2. So khớp phần hậu tố của mã đặt phòng (ví dụ 6 ký tự cuối UUID)
            if (!matched && bookingCode.contains("-")) {
                String suffix = bookingCode.substring(bookingCode.lastIndexOf("-") + 1);
                if (suffix.equalsIgnoreCase(input) || suffix.equalsIgnoreCase(cleanInput)) {
                    matched = true;
                }
            }

            // 3. So khớp số điện thoại của khách
            if (!matched && !cleanPhone.isEmpty() && !cleanInputDigits.isEmpty()) {
                if (cleanPhone.equals(cleanInputDigits) || cleanPhone.endsWith(cleanInputDigits) || cleanInputDigits.endsWith(cleanPhone)) {
                    matched = true;
                }
            }

            // 4. So khớp số CCCD/Passport nếu có
            String idCard = booking.getIdCardNumber() != null ? booking.getIdCardNumber().trim().replaceAll("[\\s\\-\\.]", "") : "";
            if (!matched && !idCard.isEmpty() && idCard.equalsIgnoreCase(cleanInput)) {
                matched = true;
            }

            if (!matched) {
                throw new BusinessException("Mã check-in hoặc số điện thoại không trùng khớp với thông tin đơn đặt phòng!");
            }
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setCheckInTime(LocalDateTime.now());
        if (booking.getBookingDetails() != null) {
            for (BookingDetail bd : booking.getBookingDetails()) {
                if (bd.getVilla() != null) {
                    if (bd.getVilla().getStatus() != VillaStatus.AVAILABLE) {
                        throw new BusinessException("Villa " + bd.getVilla().getVillaNumber() + " chưa sẵn sàng (đang " + bd.getVilla().getStatus() + "). Vui lòng chờ dọn phòng xong.");
                    }
                    bd.getVilla().setStatus(VillaStatus.OCCUPIED);
                    bd.getVilla().setCurrentGuestName(
                            booking.getUser() != null ? booking.getUser().getFullName() : "Khách lưu trú");
                    villaRepository.save(bd.getVilla());
                }
            }
        }
        
        notificationService.sendNotification("REFRESH_GANTT");
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
        booking.setCheckOutTime(LocalDateTime.now());

        // Kiểm tra chi phí Minibar / vật tư phát sinh từ các đợt kiểm kê buồng phòng
        try {
            var refillTasks = refillTaskRepository.findByHousekeepingTaskBookingId(id);
            BigDecimal minibarCharges = BigDecimal.ZERO;
            if (refillTasks != null && !refillTasks.isEmpty()) {
                for (var rt : refillTasks) {
                    if (rt.getItems() != null) {
                        for (var item : rt.getItems()) {
                            int consumed = (item.getConsumedQuantity() != null ? item.getConsumedQuantity() : 0)
                                    + (item.getDamagedQuantity() != null ? item.getDamagedQuantity() : 0);
                            if (consumed > 0 && item.getItem() != null && item.getItem().getUnitPrice() != null) {
                                minibarCharges = minibarCharges.add(item.getItem().getUnitPrice().multiply(BigDecimal.valueOf(consumed)));
                            }
                        }
                    }
                }
            }
            if (minibarCharges.compareTo(BigDecimal.ZERO) > 0) {
                booking.setTotalAmount(booking.getTotalAmount().add(minibarCharges));
                String existingNote = booking.getNote() != null ? booking.getNote() : "";
                booking.setNote(existingNote + String.format(" [Phụ thu Minibar/hỏng hóc: %,.0f VNĐ]", minibarCharges));
            }
        } catch (Exception e) {
            // Không làm gián đoạn luồng checkout nếu có lỗi tính phụ thu
        }

        if (booking.getBookingDetails() != null) {
            for (BookingDetail bd : booking.getBookingDetails()) {
                if (bd.getVilla() != null) {
                    bd.getVilla().setStatus(VillaStatus.CLEANING);
                    bd.getVilla().setCurrentGuestName(null);
                    villaRepository.save(bd.getVilla());

                    Room targetRoom = bd.getRoom();
                    if (targetRoom == null && bd.getVilla().getRooms() != null && !bd.getVilla().getRooms().isEmpty()) {
                        targetRoom = bd.getVilla().getRooms().iterator().next();
                    }
                    HousekeepingTask task = HousekeepingTask.builder()
                            .villa(bd.getVilla())
                            .room(targetRoom)
                            .booking(booking)
                            .taskType("CHECKOUT_DEEP")
                            .status("PENDING")
                            .priority("NORMAL")
                            .build();
                    housekeepingTaskRepository.save(task);
                }
            }
        }
        
        notificationService.sendNotification("REFRESH_GANTT");
        return BookingResponse.fromEntity(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminBookingItemResponse> getAdminBookings(AdminBookingFilterRequest filterRequest) {
        int pageNo = Math.max(0, filterRequest.getPage());
        int pageSize = filterRequest.getSize() > 0 ? filterRequest.getSize() : 10;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        BookingStatus statusEnum = null;
        if (filterRequest.getStatus() != null && !filterRequest.getStatus().isBlank()
                && !"ALL".equalsIgnoreCase(filterRequest.getStatus())) {
            try {
                statusEnum = BookingStatus.valueOf(filterRequest.getStatus().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // Status filter không hợp lệ → bỏ qua filter, trả tất cả
            }
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
                if (nights <= 0)
                    nights = 1;

                String gName = b.getGuestName() != null && !b.getGuestName().isBlank() ? b.getGuestName()
                        : (b.getUser() != null ? b.getUser().getFullName() : "Khách lưu trú");
                String avatar = "https://ui-avatars.com/api/?name=" + gName.replaceAll(" ", "+") + "&background=0284c7&color=fff";
                String gPhone = b.getGuestPhone() != null && !b.getGuestPhone().isBlank() ? b.getGuestPhone()
                        : (b.getUser() != null ? b.getUser().getPhone() : "");
                String gEmail = b.getGuestEmail() != null && !b.getGuestEmail().isBlank() ? b.getGuestEmail()
                        : (b.getUser() != null ? b.getUser().getEmail() : "");

                String channel = "Website Trực Tiếp";
                if (b.getPayment() != null && b.getPayment().getPaymentMethod() != null) {
                    channel = b.getPayment().getPaymentMethod();
                }

                String extraSvc = null;
                String extraIcon = null;
                if (b.getExtraServices() != null && !b.getExtraServices().isEmpty()) {
                    var bes = b.getExtraServices().get(0);
                    if (bes.getExtraService() != null) {
                        extraSvc = bes.getExtraService().getName() + " (x" + bes.getQuantity() + ")";
                        extraIcon = bes.getExtraService().getIcon() != null ? bes.getExtraService().getIcon() : "room_service";
                    }
                }

                String statusLabel;
                String statusBadgeColor;
                switch (b.getStatus()) {
                    case CHECKED_IN:
                        statusLabel = "Đang Lưu Trú";
                        statusBadgeColor = "bg-sky-50 text-sky-700 border-sky-300";
                        break;
                    case CONFIRMED:
                        statusLabel = "Đã Xác Nhận";
                        statusBadgeColor = "bg-emerald-50 text-emerald-700 border-emerald-300";
                        break;
                    case PENDING:
                        statusLabel = "Chờ Thanh Toán";
                        statusBadgeColor = "bg-amber-50 text-amber-700 border-amber-300";
                        break;
                    case CHECKED_OUT:
                        statusLabel = "Đã Trả Phòng";
                        statusBadgeColor = "bg-slate-100 text-slate-700 border-slate-300";
                        break;
                    case CANCELLED:
                    default:
                        statusLabel = "Đã Hủy";
                        statusBadgeColor = "bg-rose-50 text-rose-700 border-rose-300";
                        break;
                }

                String paymentStatusDisplay;
                if (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.CHECKED_IN || b.getStatus() == BookingStatus.CHECKED_OUT) {
                    paymentStatusDisplay = "Đã thanh toán";
                } else if (b.getStatus() == BookingStatus.CANCELLED) {
                    paymentStatusDisplay = "Đã hủy đơn";
                } else {
                    paymentStatusDisplay = "Chờ thanh toán";
                }

                items.add(AdminBookingItemResponse.builder()
                        .id(b.getId())
                        .bookingCode(b.getBookingCode())
                        .bookingDateFormatted(b.getCreatedAt() != null ? b.getCreatedAt().format(dtf) : "")
                        .guestName(gName)
                        .guestEmail(gEmail)
                        .avatarUrl(avatar)
                        .guestCountry("Việt Nam")
                        .guestPhone(gPhone)
                        .guestTier("Thành viên")
                        .tierBadgeColor("bg-slate-100 text-slate-800 border-slate-300")
                        .villaNumber(villaNum)
                        .villaTypeName(typeName)
                        .roomTypeName(typeName)
                        .checkInDate(b.getCheckInDate())
                        .checkOutDate(b.getCheckOutDate())
                        .checkInFormatted(b.getCheckInDate() != null ? b.getCheckInDate().format(dateFmt) : "")
                        .checkOutFormatted(b.getCheckOutDate() != null ? b.getCheckOutDate().format(dateFmt) : "")
                        .nights(nights)
                        .guestSummary(nights + " đêm")
                        .channel(channel)
                        .channelBadgeColor("bg-emerald-50 text-emerald-700 border-emerald-200")
                        .totalAmount(b.getTotalAmount())
                        .totalAmountDisplay(
                                b.getTotalAmount() != null ? String.format("%,.0f₫", b.getTotalAmount()) : "0₫")
                        .paymentStatusDisplay(paymentStatusDisplay)
                        .paymentMethod(b.getPayment() != null ? b.getPayment().getPaymentMethod() : null)
                        .isFullyPaid(b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.CHECKED_IN || b.getStatus() == BookingStatus.CHECKED_OUT)
                        .extraServiceName(extraSvc)
                        .extraServiceIcon(extraIcon)
                        .assignedButler(null)
                        .statusCode(b.getStatus().name())
                        .statusLabel(statusLabel)
                        .statusBadgeColor(statusBadgeColor)
                        .note(b.getNote())
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

        List<Villa> villas = villaRepository.findAllWithRelations();
        if (villas.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<GanttVillaAvailabilityResponse> rows = new ArrayList<>();
        LocalDate endDate = effStartDate.plusDays(effDays);

        List<Booking> activeBookings = bookingRepository.findActiveBookingsBetween(effStartDate, endDate);

        for (Villa villa : villas) {
            List<GanttVillaAvailabilityResponse.GanttDaySlot> slots = new ArrayList<>();
            String villaName = villa.getVillaNumber();
            String villaTypeName = villa.getVillaType() != null ? villa.getVillaType().getName() : "Villa Resort";
            String zoneName = villa.getZone() != null ? villa.getZone().getName() : "Khu Biệt Thự";

            List<Booking> villaBookings = activeBookings.stream()
                    .filter(b -> b.getBookingDetails() != null && b.getBookingDetails().stream()
                            .anyMatch(bd -> bd.getVilla() != null && bd.getVilla().getId().equals(villa.getId())))
                    .collect(Collectors.toList());

            for (int i = 0; i < effDays; i++) {
                LocalDate currentDate = effStartDate.plusDays(i);
                boolean isToday = currentDate.equals(LocalDate.now());
                String dayOfWeek = switch (currentDate.getDayOfWeek()) {
                    case MONDAY -> "T2";
                    case TUESDAY -> "T3";
                    case WEDNESDAY -> "T4";
                    case THURSDAY -> "T5";
                    case FRIDAY -> "T6";
                    case SATURDAY -> "T7";
                    case SUNDAY -> "CN";
                };
                String dateLabel = (isToday ? "H.Nay " : dayOfWeek + " ") + String.format("(%02d/%02d)", currentDate.getDayOfMonth(), currentDate.getMonthValue());

                Booking matchBooking = villaBookings.stream()
                        .filter(b -> (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.CHECKED_IN)
                                && !currentDate.isBefore(b.getCheckInDate())
                                && currentDate.isBefore(b.getCheckOutDate()))
                        .findFirst()
                        .orElse(null);

                if (matchBooking != null) {
                    boolean isStart = currentDate.equals(matchBooking.getCheckInDate());
                    int span = (int) ChronoUnit.DAYS.between(matchBooking.getCheckInDate(),
                            matchBooking.getCheckOutDate());
                    String guestName = matchBooking.getGuestName() != null && !matchBooking.getGuestName().isBlank()
                            ? matchBooking.getGuestName()
                            : (matchBooking.getUser() != null ? matchBooking.getUser().getFullName() : "Khách lưu trú");
                    String status = matchBooking.getStatus() == BookingStatus.CHECKED_IN ? "OCCUPIED" : "CONFIRMED";
                    String colorClass = matchBooking.getStatus() == BookingStatus.CHECKED_IN ? "bg-sky-600 text-white font-bold"
                            : "bg-amber-600 text-white font-bold";

                    slots.add(new GanttVillaAvailabilityResponse.GanttDaySlot(
                            dateLabel, status, guestName, span, isStart,
                            guestName + (span > 0 ? " (" + span + "Đ)" : ""),
                            colorClass, matchBooking.getId(), matchBooking.getBookingCode()));
                } else {
                    String status = "AVAILABLE";
                    String colorClass = "bg-slate-50 text-slate-400 hover:bg-slate-100";
                    String label = "Trống";
                    if (currentDate.equals(LocalDate.now())) {
                        if (villa.getStatus() == VillaStatus.CLEANING) {
                            status = "CLEANING";
                            label = "Đang dọn";
                            colorClass = "bg-amber-100 text-amber-900 border border-amber-300 font-bold";
                        } else if (villa.getStatus() == VillaStatus.MAINTENANCE) {
                            status = "MAINTENANCE";
                            label = "Bảo trì";
                            colorClass = "bg-rose-100 text-rose-700 border border-rose-300 font-bold";
                        }
                    }
                    slots.add(new GanttVillaAvailabilityResponse.GanttDaySlot(
                            dateLabel, status, null, 1, true, label, colorClass, null, null));
                }
            }

            LocalDate today = LocalDate.now();
            String statusTag = "Sẵn Sàng";
            String statusTagClass = "bg-emerald-50 text-emerald-700 border-emerald-200";
            if (villaBookings.stream().anyMatch(b -> b.getStatus() == BookingStatus.CHECKED_IN && !today.isBefore(b.getCheckInDate()) && today.isBefore(b.getCheckOutDate()))) {
                statusTag = "Đang Có Khách";
                statusTagClass = "bg-sky-50 text-sky-700 border-sky-200";
            } else if (villaBookings.stream().anyMatch(b -> b.getStatus() == BookingStatus.CONFIRMED && !today.isBefore(b.getCheckInDate()) && today.isBefore(b.getCheckOutDate()))) {
                statusTag = "Đã Đặt";
                statusTagClass = "bg-amber-50 text-amber-700 border-amber-200";
            } else if (villa.getStatus() == VillaStatus.CLEANING) {
                statusTag = "Đang Dọn";
                statusTagClass = "bg-amber-100 text-amber-900 border-amber-300";
            } else if (villa.getStatus() == VillaStatus.MAINTENANCE) {
                statusTag = "Bảo Trì";
                statusTagClass = "bg-rose-100 text-rose-700 border-rose-300";
            }
            rows.add(new GanttVillaAvailabilityResponse(villaName, villaTypeName, zoneName, statusTag, statusTagClass, slots));
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
                            s.getSpanDays(), s.getIsSpanStart(), s.getBlockLabel(), s.getColorClass(),
                            s.getBookingId(), s.getBookingCode()))
                    .collect(Collectors.toList());
            return new GanttRoomAvailabilityResponse(v.getVillaNumber(), v.getVillaTypeName(), v.getZone(), v.getStatusTag(), v.getStatusTagClass(), slots);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse createDirectBooking(com.phungvanlong.booking_hotel.dto.request.AdminDirectBookingRequest req) {
        if (req.getGuestName() == null || req.getGuestName().isBlank()) {
            throw new BusinessException("Họ tên khách hàng không được để trống");
        }
        if (req.getCheckInDate() == null || req.getCheckOutDate() == null) {
            throw new BusinessException("Vui lòng chọn ngày Check-in và Check-out");
        }
        if (!req.getCheckInDate().isBefore(req.getCheckOutDate())) {
            throw new BusinessException("Ngày trả phòng phải sau ngày nhận phòng");
        }

        Villa targetVilla = null;
        if (req.getVillaNumber() != null && !req.getVillaNumber().isBlank()) {
            targetVilla = villaRepository.findByVillaNumber(req.getVillaNumber().trim())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy căn Villa với số phòng: " + req.getVillaNumber()));
            
            long overlaps = bookingRepository.countOverlappingBookingsForVilla(
                    targetVilla.getId(), req.getCheckInDate(), req.getCheckOutDate());
            if (overlaps > 0) {
                throw new BusinessException("Căn " + targetVilla.getVillaNumber() + " đã có khách đặt trong khoảng thời gian " + req.getCheckInDate() + " - " + req.getCheckOutDate() + ". Vui lòng chọn căn khác hoặc đổi ngày!");
            }
        }
        if (targetVilla == null) {
            List<Villa> available = villaRepository.findAll().stream()
                    .filter(v -> v.getStatus() != VillaStatus.MAINTENANCE &&
                            bookingRepository.countOverlappingBookingsForVilla(v.getId(), req.getCheckInDate(), req.getCheckOutDate()) == 0)
                    .collect(Collectors.toList());
            if (!available.isEmpty()) {
                targetVilla = available.get(0);
            } else {
                throw new BusinessException("Tất cả các căn Villa đều đã kín lịch trong khoảng thời gian đã chọn!");
            }
        }

        User adminOrGuest = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng hệ thống"));

        String bookingCode = "BK-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        BigDecimal amount = req.getAmount() != null && req.getAmount().compareTo(BigDecimal.ZERO) > 0
                ? req.getAmount()
                : (targetVilla.getVillaType() != null ? targetVilla.getVillaType().getBasePrice() : new BigDecimal("5000000.00"));

        Booking booking = Booking.builder()
                .bookingCode(bookingCode)
                .checkInDate(req.getCheckInDate())
                .checkOutDate(req.getCheckOutDate())
                .totalAmount(amount)
                .status(BookingStatus.CONFIRMED)
                .guestName(req.getGuestName())
                .guestPhone(req.getGuestPhone())
                .guestEmail(req.getGuestEmail())
                .note(req.getNote())
                .user(adminOrGuest)
                .build();

        booking = bookingRepository.save(booking);

        BookingDetail bd = BookingDetail.builder()
                .booking(booking)
                .villa(targetVilla)
                .pricePerNight(amount)
                .build();
        bookingDetailRepository.save(bd);

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(amount)
                .paymentMethod(req.getPaymentMethod() != null ? req.getPaymentMethod() : "DIRECT_CASH")
                .status(PaymentStatus.SUCCESS)
                .transactionId("DIRECT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();
        paymentRepository.save(payment);

        notificationService.sendNotification("REFRESH_GANTT");
        return BookingResponse.fromEntity(booking);
    }
}
