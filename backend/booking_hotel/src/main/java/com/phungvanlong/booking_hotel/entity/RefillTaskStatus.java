package com.phungvanlong.booking_hotel.entity;

public enum RefillTaskStatus {
    PENDING,     // Chờ kho xuất cấp phát
    IN_PROGRESS, // Đang mang đến Villa bổ sung
    COMPLETED,   // Đã bổ sung đầy đủ vào Villa
    CANCELLED    // Hủy bỏ
}
