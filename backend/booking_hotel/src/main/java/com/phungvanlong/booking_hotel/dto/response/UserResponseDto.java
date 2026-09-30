package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.AuthProvider;
import com.phungvanlong.booking_hotel.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private Boolean isActive;
    private AuthProvider provider;
    private LocalDateTime createdAt;
    private Integer totalBookings;
    private Double totalSpent;
    private LocalDateTime lastBookingDate;
}
