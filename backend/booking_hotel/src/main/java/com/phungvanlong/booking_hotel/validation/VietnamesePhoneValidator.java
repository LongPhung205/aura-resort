package com.phungvanlong.booking_hotel.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class VietnamesePhoneValidator implements ConstraintValidator<VietnamesePhone, String> {

    // Hỗ trợ đầu số Việt Nam: 03x, 05x, 07x, 08x, 09x hoặc tiền tố quốc tế +84 / 84
    private static final Pattern VIETNAMESE_PHONE_PATTERN = Pattern.compile("^(0|\\+?84)(3|5|7|8|9)[0-9]{8}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Cho phép giá trị null hoặc rỗng đối với trường tùy chọn (để @NotBlank quản lý nếu là trường bắt buộc)
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        return VIETNAMESE_PHONE_PATTERN.matcher(value.trim()).matches();
    }
}
