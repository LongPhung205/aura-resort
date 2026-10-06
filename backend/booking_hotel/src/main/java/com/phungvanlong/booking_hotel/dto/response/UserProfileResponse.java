package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.AuthProvider;
import com.phungvanlong.booking_hotel.entity.Role;
import com.phungvanlong.booking_hotel.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private Role role;
    private AuthProvider provider;
    private LocalDateTime createdAt;
    private Integer totalBookings;
    private Double totalSpent;

    public static UserProfileResponse fromEntity(User user, int totalBookings, double totalSpent) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .totalBookings(totalBookings)
                .totalSpent(totalSpent)
                .build();
    }
}
