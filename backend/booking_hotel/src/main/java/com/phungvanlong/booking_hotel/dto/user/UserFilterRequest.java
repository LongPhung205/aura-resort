package com.phungvanlong.booking_hotel.dto.user;

import com.phungvanlong.booking_hotel.entity.Role;
import lombok.Data;

@Data
public class UserFilterRequest {
    private String tab = "CUSTOMER"; // "CUSTOMER", "STAFF", or "ALL"
    private String search;
    private Role role;
    private Boolean isActive;
    private int page = 0;
    private int size = 10;
}
