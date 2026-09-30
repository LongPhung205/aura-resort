package com.phungvanlong.booking_hotel.service;

public interface EmailService {
    void sendOtpEmail(String to, String otp);
    void sendPasswordResetOtpEmail(String to, String otp);
}
