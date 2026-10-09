package com.phungvanlong.booking_hotel.service;

public interface TokenBlacklistService {

    /**
     * Blacklist an access token until its expiration time.
     */
    void blacklistAccessToken(String accessToken, long ttlMs);

    /**
     * Check if an access token has been blacklisted (e.g. after logout).
     */
    boolean isAccessTokenBlacklisted(String accessToken);

    /**
     * Save active refresh token mapped to user email.
     */
    void saveRefreshToken(String email, String refreshToken, long ttlMs);

    /**
     * Get user email associated with a refresh token.
     */
    String getEmailByRefreshToken(String refreshToken);

    /**
     * Invalidate/delete a refresh token.
     */
    void deleteRefreshToken(String refreshToken);
}
