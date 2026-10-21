package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.config.MoMoConfig;
import com.phungvanlong.booking_hotel.config.PayOSConfig;
import com.phungvanlong.booking_hotel.dto.request.MoMoPaymentRequest;
import com.phungvanlong.booking_hotel.dto.response.MoMoPaymentResponse;
import com.phungvanlong.booking_hotel.entity.Booking;
import com.phungvanlong.booking_hotel.entity.BookingStatus;
import com.phungvanlong.booking_hotel.entity.Payment;
import com.phungvanlong.booking_hotel.entity.PaymentStatus;
import com.phungvanlong.booking_hotel.entity.Promotion;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.BookingRepository;
import com.phungvanlong.booking_hotel.repository.PaymentRepository;
import com.phungvanlong.booking_hotel.repository.PromotionRepository;
import com.phungvanlong.booking_hotel.service.NotificationService;
import com.phungvanlong.booking_hotel.service.PaymentService;
import com.phungvanlong.booking_hotel.util.HmacSHA256Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final MoMoConfig moMoConfig;
    private final PayOSConfig payOSConfig;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PromotionRepository promotionRepository;
    private final NotificationService notificationService;
    private final RestTemplate restTemplate;
    private final com.phungvanlong.booking_hotel.service.EmailService emailService;
    private final PayOS payOS;

    @Override
    @Transactional
    public String createMoMoPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy Booking với ID: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Đơn đặt phòng này không ở trạng thái chờ thanh toán");
        }

        String orderId = booking.getId() + "_" + UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toán đặt phòng " + booking.getId();
        Long amount = booking.getTotalAmount().longValue();
        String extraData = "";

        // Build raw signature string
        String rawSignature = "accessKey=" + moMoConfig.getAccessKey()
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + moMoConfig.getIpnUrl()
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + moMoConfig.getPartnerCode()
                + "&redirectUrl=" + moMoConfig.getRedirectUrl()
                + "&requestId=" + requestId
                + "&requestType=" + moMoConfig.getRequestType();

        String signature = HmacSHA256Utils.sign(rawSignature, moMoConfig.getSecretKey());

        MoMoPaymentRequest request = MoMoPaymentRequest.builder()
                .partnerCode(moMoConfig.getPartnerCode())
                .requestId(requestId)
                .amount(amount)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .redirectUrl(moMoConfig.getRedirectUrl())
                .ipnUrl(moMoConfig.getIpnUrl())
                .requestType(moMoConfig.getRequestType())
                .extraData(extraData)
                .lang("vi")
                .signature(signature)
                .build();

        try {
            ResponseEntity<MoMoPaymentResponse> response = restTemplate.postForEntity(
                    moMoConfig.getEndpoint(), request, MoMoPaymentResponse.class);
            
            if (response.getBody() != null && response.getBody().getResultCode() == 0) {
                return response.getBody().getPayUrl();
            } else {
                throw new BusinessException("Lỗi tạo thanh toán MoMo: " + 
                        (response.getBody() != null ? response.getBody().getMessage() : "Unknown error"));
            }
        } catch (Exception e) {
            log.error("Lỗi gọi API MoMo", e);
            throw new BusinessException("Không thể kết nối tới MoMo: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String createPayOSPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy Booking với ID: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Đơn đặt phòng này không ở trạng thái chờ thanh toán");
        }

        try {
            // Note: PayOS orderCode must be <= 9007199254740991 and unique. We can use bookingId + timestamp suffix but that might exceed Javascript MAX_SAFE_INTEGER
            // Using bookingId directly but multiplied by 1000 + some random to make it unique across tests
            long orderCode = booking.getId() * 10000 + (System.currentTimeMillis() % 10000);
            
            ItemData item = ItemData.builder()
                .name("Đặt phòng " + booking.getId())
                .price(booking.getTotalAmount().intValue())
                .quantity(1)
                .build();

            String baseReturnUrl = (payOSConfig.getReturnUrl() != null && !payOSConfig.getReturnUrl().isBlank())
                    ? payOSConfig.getReturnUrl()
                    : "http://localhost:8080/api/v1/payments/payos-return";
            String separator = baseReturnUrl.contains("?") ? "&" : "?";
            String returnUrl = baseReturnUrl + separator + "bookingId=" + booking.getId();
            String cancelUrl = baseReturnUrl + separator + "bookingId=" + booking.getId() + "&cancel=true";

            PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(booking.getTotalAmount().intValue())
                .description("Thanh toan " + booking.getId())
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .item(item)
                .build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);
            return data.getCheckoutUrl();
        } catch (Exception e) {
            log.error("Lỗi tạo PayOS link", e);
            throw new BusinessException("Không thể kết nối tới PayOS: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void processPayOSReturn(Map<String, String> params) {
        String code = params.get("code");
        String cancel = params.get("cancel");
        String bookingIdStr = params.get("bookingId");
        String orderCodeStr = params.get("orderCode");

        if ("00".equals(code) && !"true".equals(cancel) && bookingIdStr != null) {
            try {
                Long bookingId = Long.parseLong(bookingIdStr);
                Booking booking = bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new BusinessException("Booking not found: " + bookingId));

                // Optional: verify with PayOS server
                // vn.payos.type.PaymentLinkData data = payOS.getPaymentLinkInformation(Long.parseLong(orderCodeStr));
                // if (!"PAID".equals(data.getStatus())) return;

                if (booking.getStatus() == BookingStatus.PENDING) {
                    booking.setStatus(BookingStatus.CONFIRMED);
                    
                    Payment payment = new Payment();
                    payment.setBooking(booking);
                    payment.setAmount(booking.getTotalAmount());
                    payment.setPaymentMethod("PAYOS");
                    payment.setPaymentTime(LocalDateTime.now());
                    payment.setStatus(PaymentStatus.SUCCESS);
                    payment.setTransactionId(orderCodeStr);
                    
                    paymentRepository.save(payment);
                    bookingRepository.save(booking);

                    try {
                        emailService.sendBookingSuccessEmail(
                                booking.getGuestEmail(), 
                                booking.getBookingCode(), 
                                booking.getGuestName(),
                                booking.getCheckInDate(),
                                booking.getCheckOutDate(),
                                booking.getTotalAmount()
                        );
                    } catch (Exception e) {
                        log.warn("Lỗi gửi email xác nhận PayOS: {}", e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Lỗi xử lý PayOS return", e);
            }
        }
    }

    @Override
    @Transactional
    public void processPayOSWebhook(Map<String, Object> body) {
        try {
            String code = (String) body.get("code");
            if ("00".equals(code) && body.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                String orderCodeStr = String.valueOf(data.get("orderCode"));
                Long orderCode = Long.parseLong(orderCodeStr);
                
                // Móc ID Booking ra từ orderCode (nhớ lúc tạo là booking.getId() * 10000 + random)
                Long bookingId = orderCode / 10000;
                
                Booking booking = bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new BusinessException("Booking not found from webhook: " + bookingId));

                if (booking.getStatus() == BookingStatus.PENDING) {
                    booking.setStatus(BookingStatus.CONFIRMED);
                    
                    Payment payment = new Payment();
                    payment.setBooking(booking);
                    payment.setAmount(booking.getTotalAmount());
                    payment.setPaymentMethod("PAYOS");
                    payment.setPaymentTime(LocalDateTime.now());
                    payment.setStatus(PaymentStatus.SUCCESS);
                    payment.setTransactionId(orderCodeStr);
                    
                    paymentRepository.save(payment);
                    bookingRepository.save(booking);

                    try {
                        emailService.sendBookingSuccessEmail(
                                booking.getGuestEmail(), 
                                booking.getBookingCode(), 
                                booking.getGuestName(),
                                booking.getCheckInDate(),
                                booking.getCheckOutDate(),
                                booking.getTotalAmount()
                        );
                    } catch (Exception e) {
                        log.warn("Lỗi gửi email xác nhận PayOS Webhook: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Lỗi xử lý PayOS Webhook", e);
        }
    }

    @Override
    @Transactional
    public void processMoMoReturn(Map<String, String> params) {
        verifyAndProcessPayment(params);
    }

    @Override
    @Transactional
    public void processMoMoIpn(Map<String, String> params) {
        verifyAndProcessPayment(params);
    }

    private void verifyAndProcessPayment(Map<String, String> params) {
        String partnerCode = params.get("partnerCode");
        String orderId = params.get("orderId");
        String requestId = params.get("requestId");
        String amount = params.get("amount");
        String orderInfo = params.get("orderInfo");
        String orderType = params.get("orderType");
        String transId = params.get("transId");
        String resultCode = params.get("resultCode");
        String message = params.get("message");
        String payType = params.get("payType");
        String responseTime = params.get("responseTime");
        String extraData = params.get("extraData");
        String signature = params.get("signature");

        if (orderId == null || orderId.isBlank()) {
            log.warn("MoMo callback không có orderId, bỏ qua.");
            return;
        }

        Long bookingId;
        try {
            bookingId = Long.parseLong(orderId.split("_")[0]);
        } catch (Exception e) {
            log.warn("Không thể parse bookingId từ orderId: {}", orderId);
            return;
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            log.warn("Không tìm thấy Booking ID {} từ MoMo callback, bỏ qua.", bookingId);
            return;
        }

        String rawSignature = "accessKey=" + moMoConfig.getAccessKey()
                + "&amount=" + (amount != null ? amount : "")
                + "&extraData=" + (extraData != null ? extraData : "")
                + "&message=" + (message != null ? message : "")
                + "&orderId=" + (orderId != null ? orderId : "")
                + "&orderInfo=" + (orderInfo != null ? orderInfo : "")
                + "&orderType=" + (orderType != null ? orderType : "")
                + "&partnerCode=" + (partnerCode != null ? partnerCode : "")
                + "&payType=" + (payType != null ? payType : "")
                + "&requestId=" + (requestId != null ? requestId : "")
                + "&responseTime=" + (responseTime != null ? responseTime : "")
                + "&resultCode=" + (resultCode != null ? resultCode : "")
                + "&transId=" + (transId != null ? transId : "");

        String computedSignature = HmacSHA256Utils.sign(rawSignature, moMoConfig.getSecretKey());

        boolean isSuccessCode = "0".equals(resultCode);
        if (signature == null || !computedSignature.equals(signature)) {
            log.warn("Chữ ký MoMo không hợp lệ hoặc bị thiếu cho OrderId: {}", orderId);
            return; // Reject fake or unauthorized request
        }

        if (isSuccessCode) {
            // IDEMPOTENCY: nếu đã xử lý thành công rồi thì bỏ qua
            if (paymentRepository.existsByBookingIdAndStatus(bookingId, PaymentStatus.SUCCESS)) {
                log.info("Booking {} đã thanh toán trước đó, bỏ qua callback lặp.", bookingId);
                return;
            }

            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            // Cập nhật hoặc tạo Payment (upsert pattern)
            Payment payment = paymentRepository.findByBookingId(bookingId).orElse(
                Payment.builder().booking(booking).build()
            );
            payment.setAmount(booking.getTotalAmount());
            payment.setPaymentMethod("MOMO");
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(transId);
            payment.setPaymentTime(LocalDateTime.now());
            payment.setReconciliationNote("Thanh toán thành công qua MoMo");
            paymentRepository.save(payment);

            log.info("Thanh toán MoMo thành công cho Booking ID: {}", bookingId);
            notificationService.sendNotification("REFRESH_GANTT");

            // Gửi email xác nhận đặt phòng thành công
            String recipientEmail = (booking.getGuestEmail() != null && !booking.getGuestEmail().isBlank())
                    ? booking.getGuestEmail()
                    : (booking.getUser() != null ? booking.getUser().getEmail() : null);
            String recipientName = (booking.getGuestName() != null && !booking.getGuestName().isBlank())
                    ? booking.getGuestName()
                    : (booking.getUser() != null ? booking.getUser().getFullName() : "Quý khách");

            if (recipientEmail != null && !recipientEmail.isBlank()) {
                final String finalEmail = recipientEmail;
                final String finalName = recipientName;
                final String bCode = booking.getBookingCode();
                final java.time.LocalDate cIn = booking.getCheckInDate();
                final java.time.LocalDate cOut = booking.getCheckOutDate();
                final java.math.BigDecimal totalAmt = booking.getTotalAmount();
                java.util.concurrent.CompletableFuture.runAsync(() -> {
                    try {
                        emailService.sendBookingSuccessEmail(finalEmail, bCode, finalName, cIn, cOut, totalAmt);
                    } catch (Exception ex) {
                        log.error("Lỗi gửi email xác nhận đặt phòng sau thanh toán MoMo: {}", ex.getMessage());
                    }
                });
            }
        } else {
            // Thanh toán thất bại hoặc user hủy -> HỦY đơn đặt phòng, giải phóng phòng và cập nhật Payment FAILED
            log.info("Thanh toán MoMo thất bại/hủy cho Booking ID: {}, resultCode: {}, message: {}", bookingId, resultCode, message);
            
            if (booking.getStatus() == BookingStatus.PENDING) {
                booking.setStatus(BookingStatus.CANCELLED);
                booking.setExpireAt(null);
                restorePromotion(booking);
                bookingRepository.save(booking);
            }

            Payment payment = paymentRepository.findByBookingId(bookingId).orElse(
                Payment.builder().booking(booking).build()
            );
            payment.setAmount(booking.getTotalAmount());
            payment.setPaymentMethod("MOMO");
            payment.setStatus(PaymentStatus.FAILED);
            payment.setTransactionId(transId != null && !transId.isBlank() ? transId : ("FAIL-" + UUID.randomUUID().toString().substring(0, 8)));
            payment.setPaymentTime(LocalDateTime.now());
            payment.setReconciliationNote("Thanh toán thất bại / Hủy qua MoMo: " + (message != null ? message : "Mã lỗi " + resultCode));
            paymentRepository.save(payment);

            notificationService.sendNotification("REFRESH_GANTT");
        }
    }

    private void restorePromotion(Booking booking) {
        if (booking.getPromotion() != null) {
            Promotion promotion = booking.getPromotion();
            if (promotion.getQuantity() != null) {
                int oldQty = promotion.getQuantity();
                promotion.setQuantity(oldQty + 1);
                promotionRepository.save(promotion);
                log.info("Đã hoàn lại 1 lượt dùng cho voucher [{}]: {} -> {}", promotion.getCode(), oldQty, promotion.getQuantity());
            }
        }
    }

    @Override
    @Transactional
    public void simulateMoMoSuccess(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy Booking với ID: " + bookingId));

        if (paymentRepository.existsByBookingIdAndStatus(bookingId, PaymentStatus.SUCCESS)) {
            log.info("Booking {} đã thanh toán trước đó, bỏ qua giả lập.", bookingId);
            return;
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Payment payment = paymentRepository.findByBookingId(bookingId).orElse(
            Payment.builder().booking(booking).build()
        );
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod("MOMO");
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("SIM-" + UUID.randomUUID().toString().substring(0, 8));
        payment.setPaymentTime(LocalDateTime.now());
        payment.setReconciliationNote("Thanh toán MoMo thành công (Sandbox Simulator)");
        paymentRepository.save(payment);

        notificationService.sendNotification("REFRESH_GANTT");

        // Gửi email xác nhận đặt phòng thành công
        String recipientEmail = (booking.getGuestEmail() != null && !booking.getGuestEmail().isBlank())
                ? booking.getGuestEmail()
                : (booking.getUser() != null ? booking.getUser().getEmail() : null);
        String recipientName = (booking.getGuestName() != null && !booking.getGuestName().isBlank())
                ? booking.getGuestName()
                : (booking.getUser() != null ? booking.getUser().getFullName() : "Quý khách");

        if (recipientEmail != null && !recipientEmail.isBlank()) {
            final String finalEmail = recipientEmail;
            final String finalName = recipientName;
            final String bCode = booking.getBookingCode();
            final java.time.LocalDate cIn = booking.getCheckInDate();
            final java.time.LocalDate cOut = booking.getCheckOutDate();
            final java.math.BigDecimal totalAmt = booking.getTotalAmount();
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendBookingSuccessEmail(finalEmail, bCode, finalName, cIn, cOut, totalAmt);
                } catch (Exception ex) {
                    log.error("Lỗi gửi email xác nhận đặt phòng sau giả lập MoMo: {}", ex.getMessage());
                }
            });
        }
    }
}
