package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.dto.common.ApiResponse;
import com.yushan.gamification_service.dto.common.PageResponseDTO;
import com.yushan.gamification_service.dto.transaction.AdminYuanTransactionDTO;
import com.yushan.gamification_service.service.GamificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/gamification")
public class AdminGamificationController {

    @Autowired
    private GamificationService gamificationService;

    @GetMapping("/yuan/transactions")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponseDTO<AdminYuanTransactionDTO>>> getYuanTransactions(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) OffsetDateTime startDate,
            @RequestParam(required = false) OffsetDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResponseDTO<AdminYuanTransactionDTO> pagedResponse = gamificationService.findYuanTransactionsForAdmin(
                userId, startDate, endDate, page, size
        );

        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }
}
