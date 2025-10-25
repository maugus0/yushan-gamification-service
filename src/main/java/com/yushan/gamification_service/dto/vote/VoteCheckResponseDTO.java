package com.yushan.gamification_service.dto.vote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteCheckResponseDTO {
    private boolean canVote;
    private double currentYuanBalance;
    private double requiredYuan;
    private String message;
}
