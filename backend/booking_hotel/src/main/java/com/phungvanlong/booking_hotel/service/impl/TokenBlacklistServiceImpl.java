package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
    private static final String REFRESH_PREFIX = "jwt:refresh:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void blacklistAccessToken(String accessToken, long ttlMs) {
        if (accessToken == null || ttlMs <= 0) {
            return;
        }
        try {
            String key = BLACKLIST_PREFIX + accessToken;
            redisTemplate.opsForValue().set(key, "revoked", ttlMs, TimeUnit.MILLISECONDS);
            log.info("Blacklisted access token for {} ms", ttlMs);
        } catch (Exception e) {
            log.error("Failed to blacklist access token in Redis: {}", e.getMessage());
        }
    }

    @Override
    public boolean isAccessTokenBlacklisted(String accessToken) {
        if (accessToken == null) {
            return false;
        }
        try {
            String key = BLACKLIST_PREFIX + accessToken;
            Boolean hasKey = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(hasKey);
        } catch (Exception e) {
            log.warn("Redis error checking blacklisted token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void saveRefreshToken(String email, String refreshToken, long ttlMs) {
        if (refreshToken == null || email == null) {
            return;
        }
        try {
            String key = REFRESH_PREFIX + refreshToken;
            redisTemplate.opsForValue().set(key, email, ttlMs, TimeUnit.MILLISECONDS);
            log.info("Saved refresh token in Redis for email: {}", email);
        } catch (Exception e) {
            log.error("Failed to save refresh token in Redis: {}", e.getMessage());
        }
    }

    @Override
    public String getEmailByRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            return null;
        }
        try {
            String key = REFRESH_PREFIX + refreshToken;
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Failed to retrieve refresh token from Redis: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            return;
        }
        try {
            String key = REFRESH_PREFIX + refreshToken;
            redisTemplate.delete(key);
            log.info("Deleted refresh token from Redis");
        } catch (Exception e) {
            log.error("Failed to delete refresh token from Redis: {}", e.getMessage());
        }
    }
}
