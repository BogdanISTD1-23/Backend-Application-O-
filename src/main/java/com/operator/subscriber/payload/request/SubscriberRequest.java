package com.operator.subscriber.payload.request;

import com.operator.subscriber.validation.ValidPhoneNumber;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class SubscriberRequest {

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 100, message = "Фамилия должна быть от 2 до 100 символов")
    private String lastName;

    @NotBlank(message = "Номер телефона обязателен")
    @ValidPhoneNumber
    private String msisdn;

    @Email(message = "Некорректный формат email")
    private String email;

    @DecimalMin(value = "0.00", message = "Баланс не может быть отрицательным")
    private BigDecimal balance = BigDecimal.ZERO;

    private Long tariffId;

    @Pattern(regexp = "ACTIVE|SUSPENDED|BLOCKED|TERMINATED", message = "Статус должен быть: ACTIVE, SUSPENDED, BLOCKED или TERMINATED")
    private String status = "ACTIVE";

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getTariffId() {
        return tariffId;
    }

    public void setTariffId(Long tariffId) {
        this.tariffId = tariffId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
