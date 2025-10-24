package com.yushan.gamification_service.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class YuanTransactionDTO {
    private Double amount;
    private String description;
    private OffsetDateTime createdAt;

}
