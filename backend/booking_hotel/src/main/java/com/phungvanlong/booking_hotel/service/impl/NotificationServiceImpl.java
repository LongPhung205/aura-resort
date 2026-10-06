package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService, MessageListener {

    private final StringRedisTemplate redisTemplate;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    public static final String REDIS_CHANNEL = "hotel_notifications";

    @Override
    public SseEmitter createConnection(String clientId) {
        // Timeout 1h
        SseEmitter emitter = new SseEmitter(3600000L);
        emitters.put(clientId, emitter);

        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(clientId);
        });
        emitter.onError((e) -> {
            emitter.completeWithError(e);
            emitters.remove(clientId);
        });

        return emitter;
    }

    @Override
    public void sendNotification(String message) {
        // Publish to Redis instead of sending directly to emitters
        redisTemplate.convertAndSend(REDIS_CHANNEL, message);
        log.info("Published message to Redis channel '{}': {}", REDIS_CHANNEL, message);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String msgBody = new String(message.getBody());
        log.info("Received message from Redis channel '{}': {}", REDIS_CHANNEL, msgBody);
        
        // Broadcast to all local SSE clients
        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("status_update")
                        .data(msgBody));
            } catch (IOException e) {
                emitters.remove(clientId);
            }
        });
    }
}
