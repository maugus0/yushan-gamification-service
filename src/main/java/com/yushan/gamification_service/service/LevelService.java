package com.yushan.gamification_service.service;

import org.springframework.stereotype.Service;

@Service
public class LevelService {
    private static final double[] LEVEL_THRESHOLDS = {100, 500, 2000, 5000};

    /**
     * 根据给定的经验值，计算出对应的等级。
     *
     * @param exp 用户的总经验值
     * @return 计算出的等级
     */
    public int calculateLevel(Double exp) {
        // 如果经验值为 null 或是负数，默认为 1 级
        if (exp == null || exp < 0) {
            return 1;
        }

        // 遍历阈值数组，找到第一个大于当前经验值的阈值
        for (int i = 0; i < LEVEL_THRESHOLDS.length; i++) {
            if (exp < LEVEL_THRESHOLDS[i]) {
                // 等级是数组索引 + 1
                return i + 1;
            }
        }

        // 如果经验值超过了所有定义的阈值，那么就是最高等级
        return LEVEL_THRESHOLDS.length + 1;
    }
}