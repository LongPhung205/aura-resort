package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.ai.ChatResponse;
import com.phungvanlong.booking_hotel.dto.request.AiCallbackRequest;

public interface AiAgentService {
    ChatResponse chat(String sessionId, String message, String userEmail);

    void clearSession(String sessionId);

    void submitCallbackRequest(AiCallbackRequest request);
}

