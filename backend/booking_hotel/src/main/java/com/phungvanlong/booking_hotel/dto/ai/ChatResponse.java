package com.phungvanlong.booking_hotel.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String reply;           // text trả về cho user
    private String sessionId;       // echo lại sessionId để frontend lưu
    private String actionType;      // null | "SEARCH_RESULTS" | "BOOKING_CREATED" | "VILLA_DETAIL" | "LOGIN_REQUIRED"
    private Object actionPayload;   // data đính kèm (danh sách villa, booking info...)
}
