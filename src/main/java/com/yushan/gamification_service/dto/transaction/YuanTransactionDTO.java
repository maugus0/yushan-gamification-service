package com.yushan.gamification_service.dto.transaction;

import java.time.OffsetDateTime;

public class YuanTransactionDTO {
    private Double amount;
    private String description;
    private OffsetDateTime createdAt;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
