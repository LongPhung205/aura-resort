package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.CreateUserAdminRequest;
import com.phungvanlong.booking_hotel.dto.request.ResetPasswordAdminRequest;
import com.phungvanlong.booking_hotel.dto.request.UpdateUserAdminRequest;
import com.phungvanlong.booking_hotel.dto.request.UserFilterRequest;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;
import com.phungvanlong.booking_hotel.dto.response.UserResponseDto;
import com.phungvanlong.booking_hotel.dto.response.UserSummaryStatsDto;

public interface AdminUserService {
    PageResponse<UserResponseDto> getUsers(UserFilterRequest filter);
    UserSummaryStatsDto getUserStats();
    UserResponseDto getUserById(Long id);
    UserResponseDto createUser(CreateUserAdminRequest request);
    UserResponseDto updateUser(Long id, UpdateUserAdminRequest request, String currentAdminEmail);
    UserResponseDto toggleUserStatus(Long id, String currentAdminEmail);
    void resetPassword(Long id, ResetPasswordAdminRequest request);
    void deleteUser(Long id, String currentAdminEmail);
}
