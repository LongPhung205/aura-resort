package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.LoginRequest;
import com.phungvanlong.booking_hotel.dto.request.RegisterRequest;
import com.phungvanlong.booking_hotel.dto.response.AuthResponse;
import com.phungvanlong.booking_hotel.entity.Role;
import com.phungvanlong.booking_hotel.entity.User;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.UserRepository;
import com.phungvanlong.booking_hotel.security.JwtTokenProvider;
import com.phungvanlong.booking_hotel.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.phungvanlong.booking_hotel.service.EmailService;
import com.phungvanlong.booking_hotel.dto.request.VerifyOtpRequest;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.phungvanlong.booking_hotel.dto.request.ForgotPasswordRequest;
import com.phungvanlong.booking_hotel.dto.request.ResetPasswordRequest;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.phungvanlong.booking_hotel.dto.request.GoogleLoginRequest;
import com.phungvanlong.booking_hotel.entity.AuthProvider;
import org.springframework.beans.factory.annotation.Value;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${app.otp.expiration:5}")
    private long otpExpiration;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final com.phungvanlong.booking_hotel.service.TokenBlacklistService tokenBlacklistService;

    @Override
    @Transactional
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        try {
            NetHttpTransport transport = new NetHttpTransport();
            GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                
                User user = userRepository.findByEmail(email).orElse(null);
                if (user == null) {
                    user = User.builder()
                            .email(email)
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .fullName(name)
                            .role(Role.ROLE_CUSTOMER)
                            .isActive(true)
                            .provider(AuthProvider.GOOGLE)
                            .build();
                    userRepository.save(user);
                } else {
                    // Cập nhật provider nếu tài khoản email đã tồn tại (hoặc tùy logic)
                    if (user.getProvider() == AuthProvider.LOCAL) {
                        user.setProvider(AuthProvider.GOOGLE);
                        userRepository.save(user);
                    }
                }

                if (!Boolean.TRUE.equals(user.getIsActive())) {
                    throw new BusinessException("Tài khoản của bạn đã bị khóa");
                }
                
                String role = user.getRole() != null ? user.getRole().name() : "ROLE_CUSTOMER";
                com.phungvanlong.booking_hotel.security.UserPrincipal principal = 
                        com.phungvanlong.booking_hotel.security.UserPrincipal.create(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                String jwt = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), role);
                String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());
                tokenBlacklistService.saveRefreshToken(user.getEmail(), refreshToken, tokenProvider.getRefreshTokenExpirationInMs());
                
                return AuthResponse.builder()
                        .accessToken(jwt)
                        .refreshToken(refreshToken)
                        .role(role)
                        .fullName(user.getFullName() != null ? user.getFullName() : "")
                        .avatarUrl(user.getAvatar())
                        .build();
            } else {
                throw new BusinessException("Token Google không hợp lệ");
            }
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            throw new BusinessException("Lỗi xác thực Google: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BusinessException("Không tìm thấy thông tin tài khoản"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BusinessException("Tài khoản của bạn đã bị khóa");
        }

        String role = user.getRole() != null ? user.getRole().name() : "ROLE_CUSTOMER";
        String jwt = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), role);
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());
        tokenBlacklistService.saveRefreshToken(user.getEmail(), refreshToken, tokenProvider.getRefreshTokenExpirationInMs());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .role(role)
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatar())
                .build();
    }

    @Override
    public AuthResponse refreshToken(com.phungvanlong.booking_hotel.dto.request.RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        String emailFromRedis = tokenBlacklistService.getEmailByRefreshToken(refreshToken);
        String emailFromToken = tokenProvider.getUsernameFromJWT(refreshToken);

        if (emailFromRedis == null || !emailFromRedis.equalsIgnoreCase(emailFromToken)) {
            throw new BusinessException("Refresh token đã bị thu hồi hoặc không tồn tại");
        }

        User user = userRepository.findByEmail(emailFromToken)
                .orElseThrow(() -> new BusinessException("Người dùng không tồn tại"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BusinessException("Tài khoản của bạn đã bị khóa");
        }

        String role = user.getRole() != null ? user.getRole().name() : "ROLE_CUSTOMER";
        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), role);

        // Rotate refresh token
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getEmail());
        tokenBlacklistService.deleteRefreshToken(refreshToken);
        tokenBlacklistService.saveRefreshToken(user.getEmail(), newRefreshToken, tokenProvider.getRefreshTokenExpirationInMs());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .role(role)
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatar())
                .build();
    }

    @Override
    public void logout(String bearerToken, com.phungvanlong.booking_hotel.dto.request.LogoutRequest request) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            long remainingTtl = tokenProvider.getRemainingTtl(accessToken);
            if (remainingTtl > 0) {
                tokenBlacklistService.blacklistAccessToken(accessToken, remainingTtl);
            }
        }

        if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            tokenBlacklistService.deleteRefreshToken(request.getRefreshToken());
        }
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException("Email đã được sử dụng");
        }

        // Generate OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Save to Redis
        String redisKey = "register_otp:" + registerRequest.getEmail();
        try {
            redisTemplate.opsForValue().set(redisKey + ":code", otp, otpExpiration, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new BusinessException("Lỗi hệ thống khi xử lý đăng ký");
        }

        // Send Email
        emailService.sendOtpEmail(registerRequest.getEmail(), otp);
    }

    @Override
    @Transactional
    public void verifyOtp(VerifyOtpRequest request) {
        String redisKey = "register_otp:" + request.getEmail();
        String cachedOtp = redisTemplate.opsForValue().get(redisKey + ":code");
        
        if (cachedOtp == null) {
            throw new BusinessException("OTP đã hết hạn hoặc không tồn tại");
        }
        
        if (!cachedOtp.equals(request.getOtp())) {
            throw new BusinessException("OTP không chính xác");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email đã được sử dụng");
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Số điện thoại đã được sử dụng");
        }
        
        try {
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .fullName(request.getFullName())
                    .phone(request.getPhone())
                    .role(Role.ROLE_CUSTOMER)
                    .isActive(true)
                    .build();

            userRepository.save(user);
            
            // Delete from Redis
            redisTemplate.delete(redisKey + ":code");
        } catch (Exception e) {
            throw new BusinessException("Lỗi hệ thống khi xác thực OTP");
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Tài khoản không tồn tại");
        }

        // Generate OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Save to Redis
        String redisKey = "reset_otp:" + request.getEmail();
        redisTemplate.opsForValue().set(redisKey, otp, otpExpiration, TimeUnit.MINUTES);

        // Send Email
        emailService.sendPasswordResetOtpEmail(request.getEmail(), otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String redisKey = "reset_otp:" + request.getEmail();
        String cachedOtp = redisTemplate.opsForValue().get(redisKey);
        
        if (cachedOtp == null) {
            throw new BusinessException("OTP đã hết hạn hoặc không tồn tại");
        }
        
        if (!cachedOtp.equals(request.getOtp())) {
            throw new BusinessException("OTP không chính xác");
        }
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại"));
                
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        redisTemplate.delete(redisKey);
    }
}
