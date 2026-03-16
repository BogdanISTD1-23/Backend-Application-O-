package com.operator.subscriber.payload.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TariffRequest {

    @NotBlank(message = "Название тарифа обязательно")
    @Size(max = 100, message = "Название не должно превышать 100 символов")
    private String name;

    private String description;

    @NotNull(message = "Цена тарифа обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal price;

    @Min(value = 1, message = "Скорость должна быть не менее 1 Мбит/с")
    private Integer speedMbps;

    private boolean active = true;
}
