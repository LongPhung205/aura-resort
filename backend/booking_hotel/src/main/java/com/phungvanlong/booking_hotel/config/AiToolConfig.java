package com.phungvanlong.booking_hotel.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.phungvanlong.booking_hotel.dto.request.BookingRequest;
import com.phungvanlong.booking_hotel.dto.response.BookingResponse;
import com.phungvanlong.booking_hotel.dto.response.ComboPackageResponse;
import com.phungvanlong.booking_hotel.dto.response.PromotionResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.entity.Promotion;
import com.phungvanlong.booking_hotel.service.BookingService;
import com.phungvanlong.booking_hotel.service.ComboPackageService;
import com.phungvanlong.booking_hotel.service.PromotionService;
import com.phungvanlong.booking_hotel.service.VillaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Configuration
public class AiToolConfig {

    public record SearchVillaRequest(
            @JsonProperty(value = "zone", defaultValue = "all") String zone,
            @JsonProperty(value = "adults", defaultValue = "2") Integer adults,
            @JsonProperty(required = true, value = "check_in_date") String checkInDate,
            @JsonProperty(required = true, value = "check_out_date") String checkOutDate) {
    }

    @Bean
    @Description("Tìm villa trống. Tham số: zone (khu vực), adults (số người), check_in_date, check_out_date (YYYY-MM-DD).")
    public Function<SearchVillaRequest, List<VillaResponse>> searchAvailableVillas(VillaService villaService) {
        return request -> {
            try {
                LocalDate checkIn = LocalDate.parse(request.checkInDate());
                LocalDate checkOut = LocalDate.parse(request.checkOutDate());
                List<VillaResponse> villas = villaService.searchAvailableVillas(request.zone(), request.adults(), checkIn, checkOut);
                AiActionContext.set("SEARCH_RESULTS", villas);
                return villas;
            } catch (Exception e) {
                throw new RuntimeException("Ngày không hợp lệ. Vui lòng dùng định dạng YYYY-MM-DD.");
            }
        };
    }

    public record GetVillaDetailsRequest(@JsonProperty(required = true, value = "villa_id") Long villaId) {}

    @Bean
    @Description("Lấy chi tiết villa theo ID.")
    public Function<GetVillaDetailsRequest, VillaResponse> getVillaDetails(VillaService villaService) {
        return request -> {
            VillaResponse villa = villaService.getVillaById(request.villaId());
            AiActionContext.set("VILLA_DETAIL", villa);
            return villa;
        };
    }

    public record EmptyRequest() {}

    @Bean
    @Description("Lấy danh sách mã khuyến mãi đang có.")
    public Function<EmptyRequest, List<PromotionResponse>> getActivePromotions(PromotionService promotionService) {
        return request -> {
            List<PromotionResponse> promotions = promotionService.getActivePromotions();
            AiActionContext.set("PROMOTIONS", promotions);
            return promotions;
        };
    }

    public record ValidatePromoRequest(@JsonProperty(required = true, value = "code") String code) {}

    @Bean
    @Description("Kiểm tra mã khuyến mãi hợp lệ hay không.")
    public Function<ValidatePromoRequest, String> validatePromoCode(PromotionService promotionService) {
        return request -> {
            try {
                Promotion promo = promotionService.validatePromotionCode(request.code());
                String discountDisplay;
                if (promo.getDiscountType() != null && promo.getDiscountType().name().equals("PERCENTAGE")) {
                    discountDisplay = promo.getDiscountValue() + "%";
                } else {
                    discountDisplay = promo.getDiscountValue() + "đ";
                }
                return String.format("Mã hợp lệ! Giảm %s. Hiệu lực đến %s.", discountDisplay, promo.getEndDate());
            } catch (Exception e) {
                return "Mã khuyến mãi không hợp lệ hoặc đã hết hạn: " + request.code();
            }
        };
    }

    public record CreateBookingRequest(
            @JsonProperty(required = true, value = "villa_type_id") Long villaTypeId,
            @JsonProperty(required = true, value = "check_in_date") String checkInDate,
            @JsonProperty(required = true, value = "check_out_date") String checkOutDate,
            @JsonProperty(value = "promo_code") String promoCode) {}

    @Bean
    @Description("Tạo đơn đặt phòng. Yêu cầu: villa_type_id, check_in_date, check_out_date. Phải gọi hàm này để user đặt phòng.")
    public Function<CreateBookingRequest, Object> createBooking(BookingService bookingService) {
        return request -> {
            String userEmail = AiUserContext.get();
            if (userEmail == null || userEmail.isBlank()) {
                AiActionContext.set("LOGIN_REQUIRED", null);
                return "LOGIN_REQUIRED: Khách hàng chưa đăng nhập. Yêu cầu đăng nhập để đặt phòng.";
            }

            try {
                LocalDate checkIn = LocalDate.parse(request.checkInDate());
                LocalDate checkOut = LocalDate.parse(request.checkOutDate());

                BookingRequest bookingReq = BookingRequest.builder()
                        .checkInDate(checkIn)
                        .checkOutDate(checkOut)
                        .villaTypeId(request.villaTypeId())
                        .quantity(1)
                        .promotionCode(request.promoCode())
                        .build();

                BookingResponse booking = bookingService.createBooking(bookingReq, userEmail);
                AiActionContext.set("BOOKING_CREATED", booking);
                return booking;
            } catch (Exception e) {
                return "Thiếu thông tin hoặc lỗi đặt phòng: " + e.getMessage();
            }
        };
    }

    @Bean
    @Description("Lấy danh sách các gói combo ưu đãi.")
    public Function<EmptyRequest, List<ComboPackageResponse>> getComboPackages(ComboPackageService comboPackageService) {
        return request -> {
            List<ComboPackageResponse> packages = comboPackageService.getActive();
            AiActionContext.set("COMBO_PACKAGES", packages);
            return packages;
        };
    }
}
