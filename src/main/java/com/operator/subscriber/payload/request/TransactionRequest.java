package com.operator.subscriber.payload.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @NotNull(message = "ID абонента обязателен")
    private Long subscriberId;

    @NotNull(message = "Сумма обязательна")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    @DecimalMax(value = "100000.00", message = "Максимальная сумма: 100 000")
    private BigDecimal amount;

    @NotBlank(message = "Тип транзакции обязателен")
    @Pattern(regexp = "TOP_UP|PAYMENT|REFUND|WRITE_OFF", message = "Тип: TOP_UP, PAYMENT, REFUND или WRITE_OFF")
    private String type;

    @Size(max = 255, message = "Описание не более 255 символов")
    private String description;
}
