package com.yushan.gamification_service.dto.admin;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminAddYuanRequestDTO {
    @NotNull(message = "userId must not be null")
    private UUID userId;

    @NotNull(message = "amount must not be null")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    private Double amount;

    @Size(max = 500, message = "reason must be at most 500 characters")
    private String reason;
}
