package com.phungvanlong.booking_hotel.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startFieldName;
    private String endFieldName;
    private boolean allowEqual;
    private String message;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startFieldName = constraintAnnotation.startField();
        this.endFieldName = constraintAnnotation.endField();
        this.allowEqual = constraintAnnotation.allowEqual();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
            Object startObj = beanWrapper.getPropertyValue(startFieldName);
            Object endObj = beanWrapper.getPropertyValue(endFieldName);

            // Nếu một trong hai trường là null, để @NotNull trên field xử lý
            if (startObj == null || endObj == null) {
                return true;
            }

            boolean valid = true;
            if (startObj instanceof LocalDate start && endObj instanceof LocalDate end) {
                valid = allowEqual ? !start.isAfter(end) : start.isBefore(end);
            } else if (startObj instanceof LocalDateTime start && endObj instanceof LocalDateTime end) {
                valid = allowEqual ? !start.isAfter(end) : start.isBefore(end);
            }

            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(endFieldName)
                        .addConstraintViolation();
                return false;
            }

            return true;
        } catch (Exception e) {
            // Không ngắt luồng nếu class không có property này
            return true;
        }
    }
}
