package com.yushan.gamification_service.service;

import com.yushan.gamification_service.dao.UserAchievementMapper;
import com.yushan.gamification_service.entity.UserAchievement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AchievementServiceImpl implements AchievementService {

    private static final Logger logger = LoggerFactory.getLogger(AchievementServiceImpl.class);

    private static final String ACHIEVEMENT_ID_FIRST_LOGIN = "WELCOME_TO_YUSHAN";
    private static final String ACHIEVEMENT_ID_FIRST_COMMENT = "FIRST_CRY";
    private static final String ACHIEVEMENT_ID_10_COMMENTS = "ELOQUENT_SPEAKER";
    private static final String ACHIEVEMENT_ID_50_COMMENTS = "COMMENT_MASTER";
    private static final String ACHIEVEMENT_ID_FIRST_REVIEW = "REVIEW_ROOKIE";

    @Autowired
    private UserAchievementMapper userAchievementMapper;

    @Override
    @Transactional
    public void checkAndUnlockLoginAchievements(UUID userId) {
        logger.debug("Checking login achievements for user: {}", userId);
        unlockAchievementIfNotOwned(userId, ACHIEVEMENT_ID_FIRST_LOGIN);
    }

    @Override
    @Transactional
    public void checkAndUnlockCommentAchievements(UUID userId, long totalCommentCount) {
        logger.debug("Checking comment achievements for user: {}, total comments: {}", userId, totalCommentCount);

        if (totalCommentCount == 1) {
            unlockAchievementIfNotOwned(userId, ACHIEVEMENT_ID_FIRST_COMMENT);
        }
        if (totalCommentCount >= 10) {
            unlockAchievementIfNotOwned(userId, ACHIEVEMENT_ID_10_COMMENTS);
        }
        if (totalCommentCount >= 50) {
            unlockAchievementIfNotOwned(userId, ACHIEVEMENT_ID_50_COMMENTS);
        }
    }

    @Override
    @Transactional
    public void checkAndUnlockReviewAchievements(UUID userId, long totalReviewCount) {
        logger.debug("Checking review achievements for user: {}, total reviews: {}", userId, totalReviewCount);

        if (totalReviewCount == 1) {
            unlockAchievementIfNotOwned(userId, ACHIEVEMENT_ID_FIRST_REVIEW);
        }
    }

    private void unlockAchievementIfNotOwned(UUID userId, String achievementId) {
        Long existingUnlock = userAchievementMapper.findByUserIdAndAchievementId(userId, achievementId);
        if (existingUnlock == null) {
            logger.info("Unlocking achievement '{}' for user '{}'", achievementId, userId);
            UserAchievement newUserAchievement = new UserAchievement();
            newUserAchievement.setUserId(userId);
            newUserAchievement.setAchievementId(achievementId);
            userAchievementMapper.insert(newUserAchievement);
        } else {
            logger.debug("User {} already owns achievement '{}'", userId, achievementId);
        }
    }
}