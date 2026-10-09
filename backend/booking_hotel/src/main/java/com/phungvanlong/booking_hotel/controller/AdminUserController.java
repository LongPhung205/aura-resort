package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.CreateUserAdminRequest;
import com.phungvanlong.booking_hotel.dto.request.ResetPasswordAdminRequest;
import com.phungvanlong.booking_hotel.dto.request.UpdateUserAdminRequest;
import com.phungvanlong.booking_hotel.dto.request.UserFilterRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;
import com.phungvanlong.booking_hotel.dto.response.UserResponseDto;
import com.phungvanlong.booking_hotel.dto.response.UserSummaryStatsDto;
import com.phungvanlong.booking_hotel.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponseDto>>> getUsers(
            @ModelAttribute UserFilterRequest filter) {
        PageResponse<UserResponseDto> users = adminUserService.getUsers(filter);
        return ResponseEntity.ok(ApiResponse.success(users, "Lấy danh sách người dùng thành công"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserSummaryStatsDto>> getUserStats() {
        UserSummaryStatsDto stats = adminUserService.getUserStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê người dùng thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto user = adminUserService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "Lấy chi tiết người dùng thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(
            @Valid @RequestBody CreateUserAdminRequest request) {
        UserResponseDto created = adminUserService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Tạo tài khoản người dùng thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserAdminRequest request,
            Authentication authentication) {
        String currentAdminEmail = authentication != null ? authentication.getName() : "";
        UserResponseDto updated = adminUserService.updateUser(id, request, currentAdminEmail);
        return ResponseEntity.ok(ApiResponse.success(updated, "Cập nhật thông tin người dùng thành công"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponseDto>> toggleUserStatus(
            @PathVariable Long id,
            Authentication authentication) {
        String currentAdminEmail = authentication != null ? authentication.getName() : "";
        UserResponseDto updated = adminUserService.toggleUserStatus(id, currentAdminEmail);
        String action = Boolean.TRUE.equals(updated.getIsActive()) ? "Kích hoạt" : "Khóa";
        return ResponseEntity.ok(ApiResponse.success(updated, action + " tài khoản thành công"));
    }

    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordAdminRequest request) {
        adminUserService.resetPassword(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Đặt lại mật khẩu thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {
        String currentAdminEmail = authentication != null ? authentication.getName() : "";
        adminUserService.deleteUser(id, currentAdminEmail);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa tài khoản người dùng thành công"));
    }
}

