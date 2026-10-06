package com.phungvanlong.booking_hotel.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter createConnection(String clientId);
    void sendNotification(String message);
}
