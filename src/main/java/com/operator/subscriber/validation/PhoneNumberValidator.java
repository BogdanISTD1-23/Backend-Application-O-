package com.operator.subscriber.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    private static final Pattern MSISDN_PATTERN = Pattern.compile(
            "^996(500|501|502|503|504|505|507|508|509|700|701|702|703|704|705|706|707|708|709)\\d{6}$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // null/blank should be handled by @NotBlank/@NotNull where needed
        return MSISDN_PATTERN.matcher(value).matches();
    }
}

