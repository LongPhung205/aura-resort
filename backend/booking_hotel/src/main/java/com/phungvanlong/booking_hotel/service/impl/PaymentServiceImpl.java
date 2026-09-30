package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.config.MoMoConfig;
import com.phungvanlong.booking_hotel.dto.request.MoMoPaymentRequest;
import com.phungvanlong.booking_hotel.dto.response.MoMoPaymentResponse;
import com.phungvanlong.booking_hotel.entity.Booking;
import com.phungvanlong.booking_hotel.entity.BookingStatus;
import com.phungvanlong.booking_hotel.entity.Payment;
import com.phungvanlong.booking_hotel.entity.PaymentStatus;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.BookingRepository;
import com.phungvanlong.booking_hotel.repository.PaymentRepository;
import com.phungvanlong.booking_hotel.service.PaymentService;
import com.phungvanlong.booking_hotel.util.HmacSHA256Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final MoMoConfig moMoConfig;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

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

        RestTemplate restTemplate = new RestTemplate();
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

        String rawSignature = "accessKey=" + moMoConfig.getAccessKey()
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&message=" + message
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&orderType=" + orderType
                + "&partnerCode=" + partnerCode
                + "&payType=" + payType
                + "&requestId=" + requestId
                + "&responseTime=" + responseTime
                + "&resultCode=" + resultCode
                + "&transId=" + transId;

        String computedSignature = HmacSHA256Utils.sign(rawSignature, moMoConfig.getSecretKey());

        if (!computedSignature.equals(signature)) {
            log.warn("Chữ ký MoMo không hợp lệ cho OrderId: {}", orderId);
            return; // Ignore fake request
        }

        Long bookingId = Long.parseLong(orderId.split("_")[0]);
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null || booking.getStatus() != BookingStatus.PENDING) {
            return; // Tránh xử lý lại
        }

        if ("0".equals(resultCode)) {
            // Thanh toán thành công
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            Payment payment = Payment.builder()
                    .booking(booking)
                    .amount(booking.getTotalAmount())
                    .paymentMethod("MOMO") // Assuming MoMo
                    .status(PaymentStatus.SUCCESS)
                    .transactionId(transId)
                    .paymentTime(LocalDateTime.now())
                    .build();
            paymentRepository.save(payment);
            log.info("Thanh toán MoMo thành công cho Booking ID: {}", bookingId);
        } else {
            // Thanh toán thất bại hoặc user hủy -> giữ PENDING
            log.info("Thanh toán MoMo thất bại/hủy cho Booking ID: {}, message: {}", bookingId, message);
        }
    }
}
