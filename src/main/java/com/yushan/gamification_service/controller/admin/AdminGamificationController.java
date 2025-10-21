package com.yushan.gamification_service.controller.admin;

import com.yushan.gamification_service.dto.ApiResponse;
import com.yushan.gamification_service.dto.PagedResponse;
import com.yushan.gamification_service.dto.admin.AdminYuanTransactionDTO;
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
    public ResponseEntity<ApiResponse<PagedResponse<AdminYuanTransactionDTO>>> getYuanTransactions(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) OffsetDateTime startDate,
            @RequestParam(required = false) OffsetDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<AdminYuanTransactionDTO> pagedResponse = gamificationService.findYuanTransactionsForAdmin(
                userId, startDate, endDate, page, size
        );

        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }
}