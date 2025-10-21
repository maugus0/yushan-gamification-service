package com.yushan.gamification_service.dao;

import com.yushan.gamification_service.dto.achievement.AchievementDTO;
import com.yushan.gamification_service.entity.UserAchievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface UserAchievementMapper {

    int insert(UserAchievement userAchievement);

    List<UserAchievement> findByUserId(@Param("userId") UUID userId);

    Long findByUserIdAndAchievementId(@Param("userId") UUID userId, @Param("achievementId") String achievementId);

    List<AchievementDTO> findUnlockedAchievementsByUserId(@Param("userId") UUID userId);
}