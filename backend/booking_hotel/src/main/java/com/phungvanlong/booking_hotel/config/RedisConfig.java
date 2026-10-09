package com.phungvanlong.booking_hotel.config;

import com.phungvanlong.booking_hotel.service.impl.NotificationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Slf4j
@Configuration
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory, 
                                                        NotificationServiceImpl notificationService) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer() {
            @Override
            public void start() {
                try {
                    super.start();
                } catch (Exception e) {
                    log.warn("Could not start Redis message listener container (Redis server may be offline): {}", e.getMessage());
                }
            }
        };
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(notificationService, new ChannelTopic(NotificationServiceImpl.REDIS_CHANNEL));
        return container;
    }
}
