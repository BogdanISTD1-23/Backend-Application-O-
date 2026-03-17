package com.operator.subscriber.payload.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public SubscriberResponse() {
    }

    public SubscriberResponse(
            Long id,
            String firstName,
            String lastName,
            String msisdn,
            String email,
            BigDecimal balance,
            String tariffName,
            String photoUrl,
            String status,
            LocalDateTime registeredAt
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.msisdn = msisdn;
        this.email = email;
        this.balance = balance;
        this.tariffName = tariffName;
        this.photoUrl = photoUrl;
        this.status = status;
        this.registeredAt = registeredAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public static final class Builder {
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

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder msisdn(String msisdn) {
            this.msisdn = msisdn;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder tariffName(String tariffName) {
            this.tariffName = tariffName;
            return this;
        }

        public Builder photoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder registeredAt(LocalDateTime registeredAt) {
            this.registeredAt = registeredAt;
            return this;
        }

        public SubscriberResponse build() {
            return new SubscriberResponse(
                    id,
                    firstName,
                    lastName,
                    msisdn,
                    email,
                    balance,
                    tariffName,
                    photoUrl,
                    status,
                    registeredAt
            );
        }
    }
}
