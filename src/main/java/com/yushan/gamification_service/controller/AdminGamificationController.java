package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.dto.admin.AdminAddYuanRequestDTO;
import com.yushan.gamification_service.dto.common.ApiResponse;
import com.yushan.gamification_service.dto.common.PageResponseDTO;
import com.yushan.gamification_service.dto.transaction.AdminYuanTransactionDTO;
import com.yushan.gamification_service.service.GamificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Tag(name = "Admin Gamification Management", description = "Admin APIs for managing gamification system")
@RestController
@RequestMapping("/api/v1/gamification/admin")
public class AdminGamificationController {

    @Autowired
    private GamificationService gamificationService;

    @Operation(summary = "[ADMIN] Get Yuan transactions", description = "Get all Yuan transactions with filtering options for admin monitoring")
    @GetMapping("/yuan/transactions")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<PageResponseDTO<AdminYuanTransactionDTO>> getYuanTransactions(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) OffsetDateTime startDate,
            @RequestParam(required = false) OffsetDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResponseDTO<AdminYuanTransactionDTO> pagedResponse = gamificationService.findYuanTransactionsForAdmin(
                userId, startDate, endDate, page, size
        );

        return ApiResponse.success(pagedResponse);
    }

    @Operation(summary = "[ADMIN] Add Yuan to user", description = "Add Yuan to user's balance by admin")
    @PostMapping("/yuan/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> adminAddYuan(@Valid @RequestBody AdminAddYuanRequestDTO request) {
        gamificationService.adminAddYuan(request.getUserId(), request.getAmount(), request.getReason());
        
        String message = String.format("Successfully added %.2f Yuan to user %s", 
                request.getAmount(), request.getUserId());
        
        return ApiResponse.success(message);
    }
}
