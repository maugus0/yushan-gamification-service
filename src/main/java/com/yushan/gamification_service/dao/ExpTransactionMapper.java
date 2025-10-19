package com.yushan.gamification_service.dao;

import com.yushan.gamification_service.entity.ExpTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.UUID;

@Mapper
public interface ExpTransactionMapper {

    int insert(ExpTransaction transaction);

    Double sumAmountByUserId(@Param("userId") UUID userId);
}