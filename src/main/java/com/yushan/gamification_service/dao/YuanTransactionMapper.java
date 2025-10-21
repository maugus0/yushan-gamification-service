package com.yushan.gamification_service.dao;

import com.yushan.gamification_service.entity.YuanTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface YuanTransactionMapper {
    int insert(YuanTransaction transaction);
    Double sumAmountByUserId(@Param("userId") UUID userId);

    List<YuanTransaction> findByUserIdPaged(
            @Param("userId") UUID userId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    List<YuanTransaction> findWithFilters(
            @Param("userId") UUID userId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countWithFilters(
            @Param("userId") UUID userId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    int countByUserId(@Param("userId") UUID userId);
}