package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/momo/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> createMoMoPayment(@PathVariable Long bookingId) {
        String payUrl = paymentService.createMoMoPayment(bookingId);
        return ResponseEntity.ok(ApiResponse.success(payUrl, "Tạo link thanh toán MoMo thành công"));
    }

    @GetMapping("/momo-return")
    public ResponseEntity<String> processMoMoReturn(@RequestParam Map<String, String> params) {
        paymentService.processMoMoReturn(params);
        // Trong thực tế, bạn sẽ Redirect user về một trang web Frontend báo thành công/thất bại
        // Ở đây trả về chuỗi text cho đơn giản
        String message = "0".equals(params.get("resultCode")) ? "Thanh toán thành công!" : "Thanh toán bị hủy hoặc thất bại!";
        return ResponseEntity.ok(message);
    }

    @PostMapping("/momo-ipn")
    public ResponseEntity<Void> processMoMoIpn(@RequestBody Map<String, String> params) {
        // MoMo server gọi IPN sang endpoint này (Webhook)
        paymentService.processMoMoIpn(params);
        return ResponseEntity.noContent().build();
    }
}
