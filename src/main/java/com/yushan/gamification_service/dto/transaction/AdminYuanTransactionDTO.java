package com.yushan.gamification_service.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdminYuanTransactionDTO {

    private Long id;
    private UUID userId;
    private Double amount;
    private String description;
    private OffsetDateTime createdAt;

}
