package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Không cần @PreAuthorize("hasRole('USER')") - bất kỳ user đã login đều có thể thanh toán
    @PostMapping("/momo/{bookingId}")
    public ResponseEntity<ApiResponse<String>> createMoMoPayment(@PathVariable Long bookingId) {
        String payUrl = paymentService.createMoMoPayment(bookingId);
        return ResponseEntity.ok(ApiResponse.success(payUrl, "Tạo link thanh toán MoMo thành công"));
    }

    @GetMapping("/momo-return")
    public ResponseEntity<Void> processMoMoReturn(@RequestParam Map<String, String> params) {
        try {
            paymentService.processMoMoReturn(params);
        } catch (Exception e) {
            // log but don't fail - redirect anyway
        }
        // Redirect về trang frontend xử lý kết quả
        String resultCode = params.getOrDefault("resultCode", "-1");
        String orderId    = params.getOrDefault("orderId", "");
        String message    = params.getOrDefault("message", "");
        String redirectFe = "http://localhost:4200/payment/momo-result"
                + "?resultCode=" + resultCode
                + "&orderId=" + orderId
                + "&message=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.status(302).location(URI.create(redirectFe)).build();
    }

    @PostMapping("/momo-ipn")
    public ResponseEntity<Void> processMoMoIpn(@RequestBody Map<String, String> params) {
        paymentService.processMoMoIpn(params);
        return ResponseEntity.noContent().build();
    }
}
