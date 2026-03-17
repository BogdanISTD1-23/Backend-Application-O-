package com.operator.subscriber.payload.response;

import java.math.BigDecimal;

public class TariffResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer speedMbps;
    private boolean active;

    public TariffResponse() {
    }

    public TariffResponse(Long id, String name, String description, BigDecimal price, Integer speedMbps, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.speedMbps = speedMbps;
        this.active = active;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getSpeedMbps() {
        return speedMbps;
    }

    public void setSpeedMbps(Integer speedMbps) {
        this.speedMbps = speedMbps;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static final class Builder {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer speedMbps;
        private boolean active;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder speedMbps(Integer speedMbps) {
            this.speedMbps = speedMbps;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public TariffResponse build() {
            return new TariffResponse(id, name, description, price, speedMbps, active);
        }
    }
}
