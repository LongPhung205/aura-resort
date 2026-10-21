package com.phungvanlong.booking_hotel.dto.request;

import com.phungvanlong.booking_hotel.validation.VietnamesePhone;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @VietnamesePhone
    private String phone;
    
    private String avatar;
}
