package com.yushan.gamification_service.dao;

import com.yushan.gamification_service.entity.Achievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AchievementMapper {

    Optional<Achievement> findById(@Param("id") String id);

    List<Achievement> findAll();

    int insert(Achievement achievement);
}