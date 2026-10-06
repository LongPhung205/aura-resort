package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.ServiceRecoveryRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.ReviewAnalyticsResponse;
import com.phungvanlong.booking_hotel.dto.response.ReviewResponse;
import com.phungvanlong.booking_hotel.dto.response.ServiceRecoveryTicketResponse;
import com.phungvanlong.booking_hotel.service.AdminReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewRecoveryController {

    private final AdminReviewService reviewService;

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<ReviewAnalyticsResponse>> getAnalytics() {
        ReviewAnalyticsResponse analytics = reviewService.getAnalytics();
        return ResponseEntity.ok(ApiResponse.success(analytics, "Lấy phân tích CSAT và NPS thành công"));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse<ReviewResponse>> replyReview(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        String reply = payload.get("reply");
        String managerEmail = authentication != null ? authentication.getName() : null;
        ReviewResponse response = reviewService.replyReview(id, reply, managerEmail);
        return ResponseEntity.ok(ApiResponse.success(response, "Gửi phản hồi chính thức từ Ban Giám đốc thành công"));
    }

    @PostMapping("/service-recovery")
    public ResponseEntity<ApiResponse<ServiceRecoveryTicketResponse>> createRecoveryTicket(
            @Valid @RequestBody ServiceRecoveryRequest request,
            Authentication authentication) {
        String managerEmail = authentication != null ? authentication.getName() : null;
        ServiceRecoveryTicketResponse ticket = reviewService.createRecoveryTicket(request, managerEmail);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Kích hoạt quy trình Cứu vãn Dịch vụ 3 phút thành công"));
    }

    @PatchMapping("/service-recovery/{id}/resolve")
    public ResponseEntity<ApiResponse<ServiceRecoveryTicketResponse>> resolveRecoveryTicket(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Integer> payload) {
        Integer minutes = payload != null ? payload.get("resolutionMinutes") : 2;
        ServiceRecoveryTicketResponse ticket = reviewService.resolveRecoveryTicket(id, minutes);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Đóng sự cố khách hàng thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa đánh giá thành công"));
    }
}
