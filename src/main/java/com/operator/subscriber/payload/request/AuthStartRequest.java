package com.operator.subscriber.payload.request;

import com.operator.subscriber.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;

public record AuthStartRequest(
        @NotBlank(message = "Phone number is required")
        @ValidPhoneNumber(message = "Invalid phone number format")
        String phoneNumber
) {
}

