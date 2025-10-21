package com.yushan.gamification_service.dao;

import com.yushan.gamification_service.entity.DailyRewardLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
import java.util.UUID;

@Mapper
public interface DailyRewardLogMapper {

    Optional<DailyRewardLog> findByUserId(@Param("userId") UUID userId);

    int insert(DailyRewardLog log);

    int update(DailyRewardLog log);
}