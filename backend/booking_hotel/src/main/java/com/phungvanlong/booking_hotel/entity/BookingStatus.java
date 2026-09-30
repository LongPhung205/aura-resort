package com.phungvanlong.booking_hotel.entity;

public enum BookingStatus {
    PENDING,        // Chờ thanh toán (giữ chỗ tạm thời 15p)
    CONFIRMED,      // Đã thanh toán thành công / đã xác nhận
    CHECKED_IN,     // Khách đã nhận phòng thực tế
    CHECKED_OUT,    // Khách đã trả phòng
    CANCELLED       // Đã hủy (do quá hạn 15p hoặc khách chủ động hủy)
}
