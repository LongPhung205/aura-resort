package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.phungvanlong.booking_hotel.config.MoMoConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MoMoConfig moMoConfig;

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
            log.warn("Lỗi xử lý MoMo return callback, vẫn tiếp tục redirect: {}", e.getMessage());
        }
        // Redirect về trang frontend xử lý kết quả
        String resultCode = params.getOrDefault("resultCode", "-1");
        String orderId    = params.getOrDefault("orderId", "");
        String message    = params.getOrDefault("message", "");
        String targetFe   = (moMoConfig.getFrontendUrl() != null && !moMoConfig.getFrontendUrl().isBlank())
                ? moMoConfig.getFrontendUrl()
                : "http://localhost:4200/payment/momo-result";
        String redirectFe = targetFe
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

    @PostMapping("/sandbox-simulate/{bookingId}")
    public ResponseEntity<ApiResponse<String>> simulateMoMoSuccess(@PathVariable Long bookingId) {
        paymentService.simulateMoMoSuccess(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Xác nhận thanh toán giả lập Sandbox thành công", "Thành công"));
    }
}
