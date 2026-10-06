package com.phungvanlong.booking_hotel.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.phungvanlong.booking_hotel.config.AiActionContext;
import com.phungvanlong.booking_hotel.config.AiUserContext;
import com.phungvanlong.booking_hotel.dto.ai.ChatResponse;
import com.phungvanlong.booking_hotel.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AiAgentServiceImpl implements AiAgentService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final VillaService villaService;
    private final PromotionService promotionService;
    private final BookingService bookingService;
    private final ComboPackageService comboPackageService;

    // Lưu lịch sử chat theo sessionId (InMemory)
    private final Map<String, List<ObjectNode>> sessionHistory = new ConcurrentHashMap<>();

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model:qwen3:8b}")
    private String model;

    private static final String SYSTEM_PROMPT = """
            Bạn là trợ lý đặt phòng thông minh của AURA VILLA - khu nghỉ dưỡng biệt thự 5 sao tại Sầm Sơn.
            Luôn trả lời bằng tiếng Việt, thân thiện, ngắn gọn.
            LƯU Ý QUAN TRỌNG VỀ GIÁ: Khi liệt kê các căn villa, phải lấy chính xác giá từ trường basePrice để hiển thị giá (định dạng X.XXX.XXX đ/đêm). TUYỆT ĐỐI KHÔNG được tự bịa ra giá mới, không được tự ý thêm số 0 hay bớt số 0.
            Khi tạo booking, xác nhận thông tin với user trước. Nếu user chưa đăng nhập mà yêu cầu đặt phòng, thông báo cần đăng nhập.
            
            CÁC KHU VILLA TRONG RESORT (dùng đúng tên khi gọi tool searchAvailableVillas):
            - "Ngọc Trai": Biệt thự gần bãi biển riêng, hồ bơi vô cực, quản gia 24/7.
            - "Sao Biển": Tầm nhìn panorama hướng biển, Jacuzzi ban công, nội thất sang trọng.
            - "San Hô": Không gian biệt lập yên tĩnh, phòng chiếu phim, sức chứa lớn cho gia đình.
            - "all" hoặc để trống: Tìm tất cả khu vực.
            """;

    // Định nghĩa tools cho Ollama
    private static final String TOOLS_JSON = """
            [
              {
                "type": "function",
                "function": {
                  "name": "searchAvailableVillas",
                  "description": "Tìm villa trống.",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "zone":           { "type": "string",  "description": "Tên khu vực: 'Ngọc Trai', 'Sao Biển', 'San Hô' hoặc 'all'. PHẢI dùng đúng tên này." },
                      "adults":         { "type": "integer", "description": "Số người lớn" },
                      "check_in_date":  { "type": "string",  "description": "Ngày nhận phòng YYYY-MM-DD" },
                      "check_out_date": { "type": "string",  "description": "Ngày trả phòng YYYY-MM-DD" }
                    },
                    "required": ["check_in_date", "check_out_date"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "getVillaDetails",
                  "description": "Lấy chi tiết villa theo ID.",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "villa_id": { "type": "integer", "description": "ID villa" }
                    },
                    "required": ["villa_id"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "getActivePromotions",
                  "description": "Lấy danh sách mã khuyến mãi đang có.",
                  "parameters": { "type": "object", "properties": {} }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "validatePromoCode",
                  "description": "Kiểm tra mã khuyến mãi hợp lệ hay không.",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "code": { "type": "string", "description": "Mã khuyến mãi" }
                    },
                    "required": ["code"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "createBooking",
                  "description": "Tạo đơn đặt phòng.",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "villa_type_id":  { "type": "integer", "description": "ID loại villa" },
                      "check_in_date":  { "type": "string",  "description": "Ngày nhận phòng YYYY-MM-DD" },
                      "check_out_date": { "type": "string",  "description": "Ngày trả phòng YYYY-MM-DD" },
                      "promo_code":     { "type": "string",  "description": "Mã giảm giá (không bắt buộc)" }
                    },
                    "required": ["villa_type_id", "check_in_date", "check_out_date"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "getComboPackages",
                  "description": "Lấy danh sách gói combo ưu đãi.",
                  "parameters": { "type": "object", "properties": {} }
                }
              }
            ]
            """;

    public AiAgentServiceImpl(RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            VillaService villaService,
            PromotionService promotionService,
            BookingService bookingService,
            ComboPackageService comboPackageService) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
        this.villaService = villaService;
        this.promotionService = promotionService;
        this.bookingService = bookingService;
        this.comboPackageService = comboPackageService;
    }

    @Override
    public ChatResponse chat(String sessionId, String message, String userEmail) {
        try {
            AiActionContext.clear();
            AiUserContext.set(userEmail);

            // Lấy hoặc tạo lịch sử chat cho session
            List<ObjectNode> messages = sessionHistory.computeIfAbsent(sessionId, k -> {
                List<ObjectNode> history = new ArrayList<>();
                ObjectNode systemMsg = objectMapper.createObjectNode();
                systemMsg.put("role", "system");
                systemMsg.put("content", SYSTEM_PROMPT);
                history.add(systemMsg);
                return history;
            });

            // Thêm tin nhắn user
            ObjectNode userMsg = objectMapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", message);
            messages.add(userMsg);

            // Gọi Ollama với vòng lặp xử lý tool calling
            String finalReply = runAgentLoop(messages, userEmail);

            // Giới hạn history (giữ system + 30 tin nhắn gần nhất)
            if (messages.size() > 31) {
                ObjectNode systemMsg = messages.get(0);
                List<ObjectNode> trimmed = new ArrayList<>();
                trimmed.add(systemMsg);
                trimmed.addAll(messages.subList(messages.size() - 30, messages.size()));
                sessionHistory.put(sessionId, trimmed);
            }

            AiActionContext context = AiActionContext.get();
            return ChatResponse.builder()
                    .sessionId(sessionId)
                    .reply(finalReply)
                    .actionType(context != null ? context.getActionType() : null)
                    .actionPayload(context != null ? context.getActionPayload() : null)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi trong AI Agent chat: {}", e.getMessage(), e);
            return ChatResponse.builder()
                    .sessionId(sessionId)
                    .reply("Xin lỗi, tôi gặp sự cố kỹ thuật. Vui lòng thử lại sau.")
                    .build();
        } finally {
            AiUserContext.clear();
            AiActionContext.clear();
        }
    }

    private String runAgentLoop(List<ObjectNode> messages, String userEmail) throws Exception {
        JsonNode tools = objectMapper.readTree(TOOLS_JSON);

        // Tối đa 5 vòng tool calling để tránh vòng lặp vô hạn
        for (int i = 0; i < 5; i++) {
            // Tạo request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.set("messages", objectMapper.valueToTree(messages));
            requestBody.set("tools", tools);
            requestBody.put("stream", false);
            // Tắt thinking mode của Qwen3 để tránh lỗi parse "thinking" field
            ObjectNode options = objectMapper.createObjectNode();
            options.put("temperature", 0.1);
            options.put("num_predict", 2048);
            requestBody.set("options", options);

            // Gọi Ollama API
            String responseStr = restClient.post()
                    .uri(ollamaBaseUrl + "/api/chat")
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .retrieve()
                    .body(String.class);

            JsonNode response = objectMapper.readTree(responseStr);
            JsonNode messageNode = response.path("message");

            // Lấy nội dung và tool_calls từ response
            String content = messageNode.path("content").asText("");
            JsonNode toolCalls = messageNode.path("tool_calls");

            // Thêm assistant message vào history
            ObjectNode assistantMsg = objectMapper.createObjectNode();
            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", content);
            if (!toolCalls.isMissingNode() && toolCalls.isArray() && toolCalls.size() > 0) {
                assistantMsg.set("tool_calls", toolCalls);
            }
            messages.add(assistantMsg);

            // Nếu không có tool calls, trả về text
            if (toolCalls.isMissingNode() || !toolCalls.isArray() || toolCalls.size() == 0) {
                // Lọc bỏ phần <think>...</think> nếu model vẫn xuất ra
                return cleanThinkingTags(content);
            }

            // Xử lý từng tool call
            for (JsonNode toolCall : toolCalls) {
                String toolName = toolCall.path("function").path("name").asText();
                JsonNode argsNode = toolCall.path("function").path("arguments");

                // Parse arguments
                Map<String, Object> args;
                if (argsNode.isTextual()) {
                    args = objectMapper.readValue(argsNode.asText(), Map.class);
                } else {
                    args = objectMapper.convertValue(argsNode, Map.class);
                }

                // Thực thi tool
                String toolResult = executeTool(toolName, args, userEmail);
                log.info("Tool [{}] called with args: {} -> result length: {}", toolName, args, toolResult.length());

                // Thêm kết quả tool vào messages
                ObjectNode toolMsg = objectMapper.createObjectNode();
                toolMsg.put("role", "tool");
                toolMsg.put("content", toolResult);
                messages.add(toolMsg);
            }
        }

        return "Xin lỗi, tôi không thể xử lý yêu cầu này lúc này. Vui lòng thử lại.";
    }

    private String executeTool(String name, Map<String, Object> args, String userEmail) {
        try {
            return switch (name) {
                case "searchAvailableVillas" -> {
                    String zone = (String) args.getOrDefault("zone", "all");
                    int adults = args.get("adults") instanceof Number n ? n.intValue() : 2;
                    LocalDate checkIn = LocalDate.parse((String) args.get("check_in_date"));
                    LocalDate checkOut = LocalDate.parse((String) args.get("check_out_date"));
                    var villas = villaService.searchAvailableVillas(zone, adults, checkIn, checkOut);
                    AiActionContext.set("SEARCH_RESULTS", villas);
                    yield objectMapper.writeValueAsString(villas);
                }
                case "getVillaDetails" -> {
                    Long villaId = ((Number) args.get("villa_id")).longValue();
                    var villa = villaService.getVillaById(villaId);
                    AiActionContext.set("VILLA_DETAIL", villa);
                    yield objectMapper.writeValueAsString(villa);
                }
                case "getActivePromotions" -> {
                    var promotions = promotionService.getActivePromotions();
                    AiActionContext.set("PROMOTIONS", promotions);
                    yield objectMapper.writeValueAsString(promotions);
                }
                case "validatePromoCode" -> {
                    String code = (String) args.get("code");
                    try {
                        var promo = promotionService.validatePromotionCode(code);
                        yield String.format("Mã hợp lệ! Giảm %s. Hiệu lực đến %s.",
                                promo.getDiscountValue(), promo.getEndDate());
                    } catch (Exception e) {
                        yield "Mã khuyến mãi không hợp lệ hoặc đã hết hạn: " + code;
                    }
                }
                case "createBooking" -> {
                    if (userEmail == null || userEmail.isBlank()) {
                        AiActionContext.set("LOGIN_REQUIRED", null);
                        yield "LOGIN_REQUIRED: Khách hàng chưa đăng nhập. Yêu cầu đăng nhập để đặt phòng.";
                    }
                    Long villaTypeId = ((Number) args.get("villa_type_id")).longValue();
                    LocalDate checkIn = LocalDate.parse((String) args.get("check_in_date"));
                    LocalDate checkOut = LocalDate.parse((String) args.get("check_out_date"));
                    String promoCode = (String) args.getOrDefault("promo_code", null);

                    com.phungvanlong.booking_hotel.dto.request.BookingRequest bookingReq = com.phungvanlong.booking_hotel.dto.request.BookingRequest
                            .builder()
                            .checkInDate(checkIn)
                            .checkOutDate(checkOut)
                            .villaTypeId(villaTypeId)
                            .quantity(1)
                            .promotionCode(promoCode)
                            .build();

                    var booking = bookingService.createBooking(bookingReq, userEmail);
                    AiActionContext.set("BOOKING_CREATED", booking);
                    yield objectMapper.writeValueAsString(booking);
                }
                case "getComboPackages" -> {
                    var packages = comboPackageService.getActive();
                    AiActionContext.set("COMBO_PACKAGES", packages);
                    yield objectMapper.writeValueAsString(packages);
                }
                default -> "Công cụ không tồn tại: " + name;
            };
        } catch (Exception e) {
            log.error("Lỗi khi thực thi tool [{}]: {}", name, e.getMessage());
            return "Lỗi khi thực thi công cụ: " + e.getMessage();
        }
    }

    private String cleanThinkingTags(String content) {
        if (content == null)
            return "";
        // Xóa bỏ <think>...</think> block nếu Qwen3 vẫn xuất ra trong content
        return content.replaceAll("(?s)<think>.*?</think>", "").trim();
    }

    @Override
    public void clearSession(String sessionId) {
        sessionHistory.remove(sessionId);
    }
}
