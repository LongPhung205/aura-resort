package com.phungvanlong.booking_hotel.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VietnamesePhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface VietnamesePhone {

    String message() default "Số điện thoại không đúng định dạng Việt Nam (VD: 0912345678 hoặc +84912345678)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
