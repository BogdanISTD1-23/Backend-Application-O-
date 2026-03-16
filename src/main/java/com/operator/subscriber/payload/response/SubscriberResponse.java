package com.operator.subscriber.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SubscriberResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String msisdn;
    private String email;
    private BigDecimal balance;
    private String tariffName;
    private String photoUrl;
    private String status;
    private LocalDateTime registeredAt;
}
