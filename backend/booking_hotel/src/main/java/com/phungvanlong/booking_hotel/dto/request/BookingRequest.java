package com.phungvanlong.booking_hotel.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingRequest {

    @NotNull(message = "Ngày nhận phòng/villa không được để trống")
    @FutureOrPresent(message = "Ngày nhận phòng/villa không được ở quá khứ")
    private LocalDate checkInDate;

    @NotNull(message = "Ngày trả phòng/villa không được để trống")
    @Future(message = "Ngày trả phòng/villa phải ở tương lai")
    private LocalDate checkOutDate;

    // Căn Villa cụ thể (nếu khách chọn đúng 1 căn)
    private Long villaId;

    // Hạng Villa (hoặc roomTypeId đối với client cũ)
    private Long villaTypeId;
    private Long roomTypeId;

    public Long getTargetVillaTypeId() {
        return villaTypeId != null ? villaTypeId : roomTypeId;
    }

    @NotNull(message = "Số lượng Villa/căn không được để trống")
    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    @Builder.Default
    private Integer quantity = 1;

    private String note;
    private String specialRequest;
    private String paymentMethod;
    
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String estimatedArrivalTime;

    private String promotionCode;

    private List<BookingExtraServiceDto> extraServices;
}
