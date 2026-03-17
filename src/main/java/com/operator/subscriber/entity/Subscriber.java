package com.operator.subscriber.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscribers")
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "msisdn", nullable = false, unique = true, length = 20)
    private String msisdn;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @Column(name = "photo_path", length = 500)
    private String photoPath;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SubscriberStatus status = SubscriberStatus.ACTIVE;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    public Subscriber() {
    }

    public Subscriber(
            Long id,
            String firstName,
            String lastName,
            String msisdn,
            String email,
            BigDecimal balance,
            Tariff tariff,
            String photoPath,
            SubscriberStatus status,
            LocalDateTime registeredAt
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.msisdn = msisdn;
        this.email = email;
        this.balance = balance;
        this.tariff = tariff;
        this.photoPath = photoPath;
        this.status = status;
        this.registeredAt = registeredAt;
    }

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
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

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public SubscriberStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriberStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public enum SubscriberStatus {
        ACTIVE, SUSPENDED, BLOCKED, TERMINATED
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String firstName;
        private String lastName;
        private String msisdn;
        private String email;
        private BigDecimal balance = BigDecimal.ZERO;
        private Tariff tariff;
        private String photoPath;
        private SubscriberStatus status = SubscriberStatus.ACTIVE;
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

        public Builder tariff(Tariff tariff) {
            this.tariff = tariff;
            return this;
        }

        public Builder photoPath(String photoPath) {
            this.photoPath = photoPath;
            return this;
        }

        public Builder status(SubscriberStatus status) {
            this.status = status;
            return this;
        }

        public Builder registeredAt(LocalDateTime registeredAt) {
            this.registeredAt = registeredAt;
            return this;
        }

        public Subscriber build() {
            return new Subscriber(
                    id,
                    firstName,
                    lastName,
                    msisdn,
                    email,
                    balance,
                    tariff,
                    photoPath,
                    status,
                    registeredAt
            );
        }
    }
}
