package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.ai.ChatRequest;
import com.phungvanlong.booking_hotel.dto.ai.ChatResponse;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.service.AiAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiChatController {

        private final AiAgentService aiAgentService;

        /**
         * Gửi tin nhắn cho AI Agent.
         * Public endpoint — user chưa đăng nhập vẫn có thể chat (tìm villa).
         * Nếu đã đăng nhập → AI có thể tạo booking thay cho user.
         */
        @PostMapping("/chat")
        public ResponseEntity<ApiResponse<ChatResponse>> chat(
                        @RequestBody ChatRequest request,
                        Authentication authentication) {

                String userEmail = (authentication != null && authentication.isAuthenticated())
                                ? authentication.getName()
                                : null;

                log.info("AI Chat - sessionId: {}, userEmail: {}, message: {}",
                                request.getSessionId(), userEmail, request.getMessage());

                ChatResponse response = aiAgentService.chat(
                                request.getSessionId(),
                                request.getMessage(),
                                userEmail);

                return ResponseEntity.ok(ApiResponse.success(response, "OK"));
        }

        /**
         * Tiếp nhận yêu cầu gọi lại từ khách hàng qua chatbot.
         */
        @PostMapping("/callback-request")
        public ResponseEntity<ApiResponse<Void>> requestCallback(
                        @jakarta.validation.Valid @RequestBody com.phungvanlong.booking_hotel.dto.request.AiCallbackRequest request) {
                aiAgentService.submitCallbackRequest(request);
                return ResponseEntity.ok(ApiResponse.success(null, "Đã tiếp nhận yêu cầu gọi lại"));
        }

        /**
         * Xóa lịch sử hội thoại của session.
         */
        @DeleteMapping("/chat/session/{sessionId}")
        public ResponseEntity<ApiResponse<Void>> clearSession(@PathVariable String sessionId) {
                aiAgentService.clearSession(sessionId);
                return ResponseEntity.ok(ApiResponse.success(null, "Đã xóa lịch sử hội thoại"));
        }
}
