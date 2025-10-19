package com.yushan.gamification_service.service;

import com.yushan.gamification_service.dao.DailyRewardLogMapper;
import com.yushan.gamification_service.dao.ExpTransactionMapper;
import com.yushan.gamification_service.dao.YuanTransactionMapper;
import com.yushan.gamification_service.dao.UserAchievementMapper;
import com.yushan.gamification_service.dto.AchievementDTO;
import com.yushan.gamification_service.dto.GamificationStatsDTO;
import com.yushan.gamification_service.dto.YuanTransactionDTO;
import com.yushan.gamification_service.entity.DailyRewardLog;
import com.yushan.gamification_service.entity.ExpTransaction;
import com.yushan.gamification_service.entity.YuanTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class GamificationServiceImpl implements GamificationService {

    private static final Logger logger = LoggerFactory.getLogger(GamificationServiceImpl.class);

    @Value("${gamification.rewards.daily-login-exp:5}")
    private double dailyLoginExp;

    @Value("${gamification.rewards.daily-login-yuan:1}")
    private double dailyLoginYuan;

    @Value("${gamification.rewards.comment-exp:5}")
    private double commentExp;

    @Value("${gamification.rewards.review-exp:5}")
    private double reviewExp;

    private final DailyRewardLogMapper dailyRewardLogMapper;
    private final ExpTransactionMapper expTransactionMapper;
    private final YuanTransactionMapper yuanTransactionMapper;
    private final LevelService levelService;
    private final AchievementService achievementService;
    private final UserAchievementMapper userAchievementMapper;

    public GamificationServiceImpl(DailyRewardLogMapper dailyRewardLogMapper,
                                   ExpTransactionMapper expTransactionMapper,
                                   YuanTransactionMapper yuanTransactionMapper,
                                   LevelService levelService,
                                   AchievementService achievementService,
                                   UserAchievementMapper userAchievementMapper) {
        this.dailyRewardLogMapper = dailyRewardLogMapper;
        this.expTransactionMapper = expTransactionMapper;
        this.yuanTransactionMapper = yuanTransactionMapper;
        this.levelService = levelService;
        this.achievementService = achievementService;
        this.userAchievementMapper = userAchievementMapper;
    }

    @Override
    @Transactional
    public void processUserLogin(UUID userId) {
        logger.info("Processing login for user: {}", userId);

        LocalDate today = LocalDate.now();
        Optional<DailyRewardLog> rewardLogOpt = dailyRewardLogMapper.findByUserId(userId);

        if (rewardLogOpt.isPresent() && rewardLogOpt.get().getLastRewardDate().isEqual(today)) {
            logger.info("User {} has already claimed the daily reward today.", userId);
            achievementService.checkAndUnlockLoginAchievements(userId);
            return;
        }

        logger.info("Awarding daily login reward to user: {}", userId);
        // (EXP)
        ExpTransaction expTransaction = new ExpTransaction();
        expTransaction.setUserId(userId);
        expTransaction.setAmount(dailyLoginExp);
        expTransaction.setReason("Daily Login Reward");
        expTransactionMapper.insert(expTransaction);
        logger.debug("Inserted EXP transaction for user: {}", userId);

        // (Yuan)
        YuanTransaction yuanTransaction = new YuanTransaction();
        yuanTransaction.setUserId(userId);
        yuanTransaction.setAmount(dailyLoginYuan);
        yuanTransaction.setDescription("Daily Login Reward");
        yuanTransactionMapper.insert(yuanTransaction);
        logger.debug("Inserted Yuan transaction for user: {}", userId);

        if (rewardLogOpt.isPresent()) {
            DailyRewardLog rewardLog = rewardLogOpt.get();
            rewardLog.setLastRewardDate(today);
            dailyRewardLogMapper.update(rewardLog);
            logger.debug("Updated daily reward log for user: {}", userId);
        } else {
            DailyRewardLog newRewardLog = new DailyRewardLog();
            newRewardLog.setUserId(userId);
            newRewardLog.setLastRewardDate(today);
            dailyRewardLogMapper.insert(newRewardLog);
            logger.debug("Created new daily reward log for user: {}", userId);
        }

        logger.info("Successfully processed login and awarded daily reward for user: {}", userId);
        achievementService.checkAndUnlockLoginAchievements(userId);
    }

    @Override
    @Transactional
    public void processUserComment(UUID userId, Long commentId) {
        logger.info("Processing comment event for user: {}, commentId: {}", userId, commentId);

        ExpTransaction expTransaction = new ExpTransaction();
        expTransaction.setUserId(userId);
        expTransaction.setAmount(commentExp);
        expTransaction.setReason("Posted a comment with ID: " + commentId);
        expTransactionMapper.insert(expTransaction);
        logger.debug("Awarded {} EXP to user {} for comment {}", commentExp, userId, commentId);

        long totalCommentCount = getSimulatedTotalCommentCount(userId);

        achievementService.checkAndUnlockCommentAchievements(userId, totalCommentCount);
    }

    @Override
    @Transactional
    public void processUserReview(UUID userId, Long reviewId) {
        logger.info("Processing review event for user: {}, reviewId: {}", userId, reviewId);

        ExpTransaction expTransaction = new ExpTransaction();
        expTransaction.setUserId(userId);
        expTransaction.setAmount(reviewExp);
        expTransaction.setReason("Posted a review with ID: " + reviewId);
        expTransactionMapper.insert(expTransaction);
        logger.debug("Awarded {} EXP to user {} for review {}", reviewExp, userId, reviewId);

        long totalReviewCount = getSimulatedTotalReviewCount(userId);

        achievementService.checkAndUnlockReviewAchievements(userId, totalReviewCount);
    }

    /**
     * 【TEST】
     */
    private long getSimulatedTotalCommentCount(UUID userId) {
        return TempCounter.incrementAndGet(userId, "comment");
    }

    /**
     * 【TEST】
     */
    private long getSimulatedTotalReviewCount(UUID userId) {
        return TempCounter.incrementAndGet(userId, "review");
    }

    /**
     * 【TEST】
     */
    static class TempCounter {
        private static final Map<String, Map<UUID, Long>> userActionCounts = new ConcurrentHashMap<>();

        public static long incrementAndGet(UUID userId, String actionType) {
            userActionCounts.computeIfAbsent(actionType, k -> new ConcurrentHashMap<>());
            return userActionCounts.get(actionType).merge(userId, 1L, Long::sum);
        }
    }

    @Override
    public GamificationStatsDTO getGamificationStatsForUser(UUID userId) {
        Double totalExp = expTransactionMapper.sumAmountByUserId(userId);
        Double yuanBalance = yuanTransactionMapper.sumAmountByUserId(userId);

        totalExp = (totalExp == null) ? 0.0 : totalExp;
        yuanBalance = (yuanBalance == null) ? 0.0 : yuanBalance;

        int currentLevel = levelService.calculateLevel(totalExp);

        Double expForNextLevel = levelService.getExpForNextLevel(currentLevel);

        return new GamificationStatsDTO(currentLevel, totalExp, expForNextLevel, yuanBalance);
    }

    @Override
    public List<YuanTransactionDTO> getTransactionHistory(UUID userId, int page, int size) {
        int offset = page * size;

        List<YuanTransaction> transactions = yuanTransactionMapper.findByUserIdPaged(userId, offset, size);

        return transactions.stream()
                .map(t -> {
                    YuanTransactionDTO dto = new YuanTransactionDTO();
                    dto.setAmount(t.getAmount());
                    dto.setDescription(t.getDescription());
                    dto.setCreatedAt(t.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementDTO> getUnlockedAchievements(UUID userId) {
        return userAchievementMapper.findUnlockedAchievementsByUserId(userId);
    }
}