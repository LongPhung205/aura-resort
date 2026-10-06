package com.phungvanlong.booking_hotel.service;

public interface EmailService {
    void sendOtpEmail(String to, String otp);
    void sendPasswordResetOtpEmail(String to, String otp);
    void sendBookingSuccessEmail(String to, String bookingCode, String guestName);
    void sendBookingSuccessEmail(String to, String bookingCode, String guestName, java.time.LocalDate checkInDate, java.time.LocalDate checkOutDate, java.math.BigDecimal totalAmount);
}
