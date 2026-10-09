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
import java.time.temporal.ChronoUnit;
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
    private final ExtraServiceService extraServiceService;

    // Lưu lịch sử chat theo sessionId (InMemory)
    private final Map<String, List<ObjectNode>> sessionHistory = new ConcurrentHashMap<>();

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model:qwen3:8b}")
    private String model;

    private static final String SYSTEM_PROMPT = """
            Bạn là trợ lý tư vấn và đặt phòng thông minh của khu nghỉ dưỡng 5 sao AURA VILLA tại Sầm Sơn.
            Luôn trả lời bằng tiếng Việt, lịch thiệp, chu đáo, súc tích (dưới 150 từ).
            
            QUY TẮC BẮT BUỘC KHI TƯ VẤN:
            1. Khi khách hỏi tìm phòng, tìm villa, hỏi còn phòng trống, hoặc nhắc đến tên khu vực ('ngọc trai', 'sao biển', 'san hô') hoặc ngày tháng: BẮT BUỘC gọi ngay công cụ searchAvailableVillas để tra cứu dữ liệu thực tế. KHÔNG ĐƯỢC hỏi lại khách khu vực nào trước khi gọi công cụ. Nếu khách không nói khu vực, tự động truyền zone="all".
            2. Hôm nay là ngày %s. Nếu khách nói 'hôm nay' hoặc không nói rõ ngày: lấy ngày nhận phòng là hôm nay (%s) và ngày trả phòng là ngày mai (%s).
            3. Khi khách hỏi về TIỆN ÍCH & DỊCH VỤ BỔ SUNG (Spa, Massage, BBQ tại villa, xe đưa đón sân bay, thuê xe máy/xe điện, tiệc, ăn uống, giặt ủi,...): BẮT BUỘC gọi công cụ getExtraServices để lấy danh sách giá và thông tin chính xác. TUYỆT ĐỐI KHÔNG tự bịa giá.
            4. Khi khách muốn ĐẶT PHÒNG hoặc đồng ý chọn một căn villa: Gọi ngay công cụ prepareBookingDraft để tạo bản nháp đơn đặt phòng (tính số đêm, tiền phòng, áp voucher) và kích hoạt thẻ xác nhận trực quan cho khách. KHÔNG nói rằng đã đặt phòng thành công, mà hãy thông báo đã chuẩn bị đơn và mời khách kiểm tra bấm nút 'Xác nhận đặt phòng' trên màn hình.
            5. Khi khách yêu cầu GẶP LỄ TÂN / TƯ VẤN VIÊN NGƯỜI THẬT, có khiếu nại, sự cố, hoặc cần hỗ trợ đặc biệt: Gọi ngay công cụ requestHumanSupport để mở thẻ liên hệ Lễ tân trực tiếp 24/7.
            
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
                  "description": "Tìm villa trống theo khu vực và ngày nhận/trả phòng.",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "zone":           { "type": "string",  "description": "Tên khu vực: 'Ngọc Trai', 'Sao Biển', 'San Hô' hoặc 'all'." },
                      "adults":         { "type": "integer", "description": "Số người lớn" },
                      "check_in_date":  { "type": "string",  "description": "Ngày nhận phòng YYYY-MM-DD" },
                      "check_out_date": { "type": "string",  "description": "Ngày trả phòng YYYY-MM-DD" }
                    }
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
                  "name": "getExtraServices",
                  "description": "Lấy danh sách tiện ích và dịch vụ bổ sung của resort (Spa & Massage, BBQ tại villa, xe đưa đón sân bay, thuê xe máy/xe điện, tiệc, ăn uống,...)",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "type": { "type": "string", "description": "Loại dịch vụ tùy chọn: 'SPA', 'DINING', 'TRANSPORT', 'TOUR', 'EVENT' hoặc 'all'" }
                    }
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
                  "name": "prepareBookingDraft",
                  "description": "Chuẩn bị bản nháp đơn đặt phòng (tính số đêm, tiền phòng, áp voucher) và tạo thẻ xác nhận để khách chủ động bấm xác nhận đặt phòng.",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "villa_id":       { "type": "integer", "description": "ID của villa khách chọn" },
                      "villa_type_id":  { "type": "integer", "description": "ID loại villa (nếu có)" },
                      "check_in_date":  { "type": "string",  "description": "Ngày nhận phòng YYYY-MM-DD" },
                      "check_out_date": { "type": "string",  "description": "Ngày trả phòng YYYY-MM-DD" },
                      "adults":         { "type": "integer", "description": "Số lượng người lớn" },
                      "promo_code":     { "type": "string",  "description": "Mã giảm giá (không bắt buộc)" }
                    },
                    "required": ["check_in_date", "check_out_date"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "requestHumanSupport",
                  "description": "Kết nối khách hàng tới tư vấn viên / lễ tân trực tiếp khi khách muốn gặp người thật hoặc cần hỗ trợ đặc biệt.",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "reason": { "type": "string", "description": "Lý do cần hỗ trợ" }
                    }
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
            ComboPackageService comboPackageService,
            ExtraServiceService extraServiceService) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
        this.villaService = villaService;
        this.promotionService = promotionService;
        this.bookingService = bookingService;
        this.comboPackageService = comboPackageService;
        this.extraServiceService = extraServiceService;
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
                LocalDate today = LocalDate.now();
                systemMsg.put("content", String.format(SYSTEM_PROMPT, today, today, today.plusDays(1)));
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
            options.put("num_predict", 500);
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
                    if (zone == null || zone.isBlank()) zone = "all";
                    int adults = args.get("adults") instanceof Number n ? n.intValue() : 2;

                    LocalDate checkIn;
                    try {
                        String ci = (String) args.get("check_in_date");
                        checkIn = (ci != null && !ci.isBlank()) ? LocalDate.parse(ci) : LocalDate.now();
                    } catch (Exception e) {
                        checkIn = LocalDate.now();
                    }

                    LocalDate checkOut;
                    try {
                        String co = (String) args.get("check_out_date");
                        checkOut = (co != null && !co.isBlank()) ? LocalDate.parse(co) : checkIn.plusDays(1);
                    } catch (Exception e) {
                        checkOut = checkIn.plusDays(1);
                    }
                    if (!checkOut.isAfter(checkIn)) {
                        checkOut = checkIn.plusDays(1);
                    }

                    var villas = villaService.searchAvailableVillas(zone, adults, checkIn, checkOut);
                    AiActionContext.set("SEARCH_RESULTS", villas);

                    // Trả về danh sách tóm tắt tối đa 5 căn để LLM đọc nhanh chóng
                    List<Map<String, Object>> summary = villas.stream().limit(5).map(v -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", v.getId());
                        map.put("villaNumber", v.getVillaNumber());
                        map.put("villaType", v.getVillaTypeName() != null ? v.getVillaTypeName() : "");
                        map.put("zone", v.getZone());
                        map.put("basePrice", v.getBasePrice() != null ? v.getBasePrice().longValue() : 0);
                        map.put("bedroomCount", v.getBedroomCount());
                        map.put("imageUrl", v.getImageUrl());
                        map.put("description", v.getOverviewDescription());
                        return map;
                    }).toList();

                    yield objectMapper.writeValueAsString(summary);
                }
                case "getVillaDetails" -> {
                    Long villaId = ((Number) args.get("villa_id")).longValue();
                    var villa = villaService.getVillaById(villaId);
                    AiActionContext.set("VILLA_DETAIL", villa);
                    yield objectMapper.writeValueAsString(villa);
                }
                case "getExtraServices" -> {
                    String typeFilter = (String) args.getOrDefault("type", null);
                    var allServices = extraServiceService.getAllServices();
                    var activeServices = allServices.stream()
                            .filter(s -> Boolean.TRUE.equals(s.getIsActive()))
                            .filter(s -> {
                                if (typeFilter == null || typeFilter.isBlank() || "all".equalsIgnoreCase(typeFilter)) {
                                    return true;
                                }
                                return s.getType() != null && s.getType().equalsIgnoreCase(typeFilter);
                            })
                            .toList();

                    AiActionContext.set("EXTRA_SERVICES", activeServices);

                    List<Map<String, Object>> summary = activeServices.stream().limit(8).map(s -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", s.getId());
                        map.put("name", s.getName());
                        map.put("type", s.getType());
                        map.put("price", s.getPrice() != null ? s.getPrice().longValue() : 0);
                        map.put("unit", s.getUnit());
                        map.put("description", s.getDescription());
                        return map;
                    }).toList();

                    yield objectMapper.writeValueAsString(summary);
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
                case "prepareBookingDraft", "createBooking" -> {
                    // Safe Booking Flow: Lập bản nháp để khách chủ động bấm xác nhận
                    Long villaId = null;
                    Long villaTypeId = null;
                    if (args.containsKey("villa_id") && args.get("villa_id") != null) {
                        villaId = ((Number) args.get("villa_id")).longValue();
                    }
                    if (args.containsKey("villa_type_id") && args.get("villa_type_id") != null) {
                        villaTypeId = ((Number) args.get("villa_type_id")).longValue();
                    }

                    LocalDate checkIn;
                    try {
                        String ci = (String) args.get("check_in_date");
                        checkIn = (ci != null && !ci.isBlank()) ? LocalDate.parse(ci) : LocalDate.now();
                    } catch (Exception e) {
                        checkIn = LocalDate.now();
                    }

                    LocalDate checkOut;
                    try {
                        String co = (String) args.get("check_out_date");
                        checkOut = (co != null && !co.isBlank()) ? LocalDate.parse(co) : checkIn.plusDays(1);
                    } catch (Exception e) {
                        checkOut = checkIn.plusDays(1);
                    }
                    if (!checkOut.isAfter(checkIn)) {
                        checkOut = checkIn.plusDays(1);
                    }

                    long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
                    if (nights <= 0) nights = 1;

                    String promoCode = (String) args.getOrDefault("promo_code", null);
                    int adults = args.get("adults") instanceof Number n ? n.intValue() : 2;

                    com.phungvanlong.booking_hotel.dto.response.VillaResponse villa = null;
                    if (villaId != null) {
                        try {
                            villa = villaService.getVillaById(villaId);
                            if (villa != null && villaTypeId == null) {
                                villaTypeId = villa.getVillaTypeId();
                            }
                        } catch (Exception ignored) {}
                    }

                    if (villa == null && villaTypeId != null) {
                        var availableVillas = villaService.searchAvailableVillas("all", adults, checkIn, checkOut);
                        Long targetTypeId = villaTypeId;
                        villa = availableVillas.stream()
                                .filter(v -> targetTypeId.equals(v.getVillaTypeId()))
                                .findFirst()
                                .orElse(null);
                    }

                    String villaName = (villa != null && villa.getVillaTypeName() != null)
                            ? villa.getVillaTypeName() + (villa.getVillaNumber() != null ? " (" + villa.getVillaNumber() + ")" : "")
                            : "Biệt thự Aura Resort";
                    long pricePerNight = (villa != null && villa.getBasePrice() != null) ? villa.getBasePrice().longValue() : 3500000L;
                    long estimatedTotal = pricePerNight * nights;
                    String imageUrl = (villa != null) ? villa.getImageUrl() : null;

                    Map<String, Object> draft = new HashMap<>();
                    draft.put("villaId", villa != null ? villa.getId() : villaId);
                    draft.put("villaTypeId", villaTypeId != null ? villaTypeId : (villa != null ? villa.getVillaTypeId() : null));
                    draft.put("villaName", villaName);
                    draft.put("villaNumber", villa != null ? villa.getVillaNumber() : "");
                    draft.put("zone", villa != null ? villa.getZone() : "Ngọc Trai");
                    draft.put("checkInDate", checkIn.toString());
                    draft.put("checkOutDate", checkOut.toString());
                    draft.put("nights", nights);
                    draft.put("adults", adults);
                    draft.put("pricePerNight", pricePerNight);
                    draft.put("estimatedTotal", estimatedTotal);
                    draft.put("promoCode", promoCode);
                    draft.put("imageUrl", imageUrl);
                    draft.put("isLoggedIn", userEmail != null && !userEmail.isBlank());
                    draft.put("userEmail", userEmail != null ? userEmail : "");

                    AiActionContext.set("CONFIRM_BOOKING_DRAFT", draft);

                    yield String.format("ĐÃ LẬP BẢN NHÁP ĐẶT PHÒNG THÀNH CÔNG: %s, từ %s đến %s (%d đêm), tạm tính: %s đ. Vui lòng mời khách kiểm tra và bấm 'Xác nhận đặt phòng' trên thẻ đơn.",
                            villaName, checkIn, checkOut, nights, String.format("%,d", estimatedTotal));
                }
                case "requestHumanSupport" -> {
                    String reason = (String) args.getOrDefault("reason", "Khách hàng cần tư vấn và hỗ trợ trực tiếp từ lễ tân");
                    Map<String, Object> handoff = new HashMap<>();
                    handoff.put("hotline", "0901 234 567");
                    handoff.put("receptionEmail", "reception@auraresort.com");
                    handoff.put("zaloUrl", "https://zalo.me/0901234567");
                    handoff.put("operatingHours", "24/7 (Phục vụ liên tục)");
                    handoff.put("reason", reason);
                    handoff.put("userEmail", userEmail != null ? userEmail : "");

                    AiActionContext.set("HUMAN_HANDOFF", handoff);

                    yield "Đã kích hoạt thẻ kết nối trực tiếp với Bộ phận Lễ tân & CSKH 24/7 của resort. Hãy mời khách gọi hotline hoặc nhấn các lựa chọn trên thẻ hỗ trợ.";
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
