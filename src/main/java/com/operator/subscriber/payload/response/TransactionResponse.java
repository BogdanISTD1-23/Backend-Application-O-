package com.operator.subscriber.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {

    private Long id;
    private Long subscriberId;
    private String subscriberMsisdn;
    private BigDecimal amount;
    private String type;
    private String description;
    private LocalDateTime createdAt;
}
