package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.LoginRequest;
import com.phungvanlong.booking_hotel.dto.request.RegisterRequest;
import com.phungvanlong.booking_hotel.dto.request.VerifyOtpRequest;
import com.phungvanlong.booking_hotel.dto.response.AuthResponse;

import com.phungvanlong.booking_hotel.dto.request.ForgotPasswordRequest;
import com.phungvanlong.booking_hotel.dto.request.ResetPasswordRequest;
import com.phungvanlong.booking_hotel.dto.request.GoogleLoginRequest;
import com.phungvanlong.booking_hotel.dto.request.RefreshTokenRequest;
import com.phungvanlong.booking_hotel.dto.request.LogoutRequest;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
    void verifyOtp(VerifyOtpRequest verifyOtpRequest);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    AuthResponse googleLogin(GoogleLoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String accessToken, LogoutRequest request);
}
