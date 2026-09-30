package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.LoginRequest;
import com.phungvanlong.booking_hotel.dto.request.RegisterRequest;
import com.phungvanlong.booking_hotel.dto.request.VerifyOtpRequest;
import com.phungvanlong.booking_hotel.dto.request.ForgotPasswordRequest;
import com.phungvanlong.booking_hotel.dto.request.ResetPasswordRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.AuthResponse;
import com.phungvanlong.booking_hotel.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse token = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(token, "Đăng nhập thành công"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Mã OTP đã được gửi đến email của bạn"));
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        authService.verifyOtp(verifyOtpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Xác thực OTP và đăng ký tài khoản thành công"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Mã OTP khôi phục mật khẩu đã được gửi đến email của bạn"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Khôi phục mật khẩu thành công. Vui lòng đăng nhập lại"));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@Valid @RequestBody com.phungvanlong.booking_hotel.dto.request.GoogleLoginRequest request) {
        AuthResponse token = authService.googleLogin(request);
        return ResponseEntity.ok(ApiResponse.success(token, "Đăng nhập bằng Google thành công"));
    }
}
