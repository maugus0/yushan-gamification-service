package com.yushan.gamification_service.service;

import com.yushan.gamification_service.dao.DailyRewardLogMapper;
import com.yushan.gamification_service.dao.ExpTransactionMapper;
import com.yushan.gamification_service.dao.YuanTransactionMapper;
import com.yushan.gamification_service.dao.UserAchievementMapper;
import com.yushan.gamification_service.dto.achievement.AchievementDTO;
import com.yushan.gamification_service.dto.stats.GamificationStatsDTO;
import com.yushan.gamification_service.dto.transaction.YuanTransactionDTO;
import com.yushan.gamification_service.entity.DailyRewardLog;
import com.yushan.gamification_service.entity.ExpTransaction;
import com.yushan.gamification_service.entity.YuanTransaction;
import com.yushan.gamification_service.dto.event.LevelUpEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yushan.gamification_service.dto.common.PageResponseDTO;
import com.yushan.gamification_service.dto.transaction.AdminYuanTransactionDTO;
import java.time.OffsetDateTime;

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

    @Autowired
    private DailyRewardLogMapper dailyRewardLogMapper;
    
    @Autowired
    private ExpTransactionMapper expTransactionMapper;
    
    @Autowired
    private YuanTransactionMapper yuanTransactionMapper;
    
    @Autowired
    private LevelService levelService;
    
    @Autowired
    private AchievementService achievementService;
    
    @Autowired
    private UserAchievementMapper userAchievementMapper;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String INTERNAL_EVENTS_TOPIC = "internal_gamification_events";

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

        checkLevelUpAndPublishEvent(userId, dailyLoginExp);
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

        checkLevelUpAndPublishEvent(userId, commentExp);
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

        checkLevelUpAndPublishEvent(userId, reviewExp);
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
    private long getSimulatedTotalVoteCount(UUID userId) {
        return TempCounter.incrementAndGet(userId, "vote");
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

        double totalExpValue = (totalExp == null) ? 0.0 : totalExp;
        double yuanBalanceValue = (yuanBalance == null) ? 0.0 : yuanBalance;

        int currentLevel = levelService.calculateLevel(totalExpValue);

        Double expForNextLevel = levelService.getExpForNextLevel(currentLevel);

        return new GamificationStatsDTO(currentLevel, totalExpValue, expForNextLevel, yuanBalanceValue);
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

    @Override
    @Transactional
    public void processUserVote(UUID userId) {
        logger.info("Processing vote event for user: {}", userId);

        YuanTransaction yuanTransaction = new YuanTransaction();
        yuanTransaction.setUserId(userId);
        yuanTransaction.setAmount(-1.0);
        yuanTransaction.setDescription("Voted on a novel");
        yuanTransactionMapper.insert(yuanTransaction);

        logger.info("Deducted 1 Yuan from user {} for voting.", userId);
    }

    private void checkLevelUpAndPublishEvent(UUID userId, Double expGained) {
        Double currentTotalExp = expTransactionMapper.sumAmountByUserId(userId);
        if (currentTotalExp == null) currentTotalExp = 0.0;

        int currentLevel = levelService.calculateLevel(currentTotalExp);

        Double previousTotalExp = currentTotalExp - expGained;
        int previousLevel = levelService.calculateLevel(previousTotalExp);

        if (currentLevel > previousLevel) {
            logger.info("User {} leveled up from {} to {}!", userId, previousLevel, currentLevel);
            LevelUpEvent event = new LevelUpEvent(userId, currentLevel);
            kafkaTemplate.send(INTERNAL_EVENTS_TOPIC, event);
            logger.info("Published LevelUpEvent to topic '{}'", INTERNAL_EVENTS_TOPIC);
        }
    }

    @Override
    public PageResponseDTO<AdminYuanTransactionDTO> findYuanTransactionsForAdmin(
            UUID userId,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            int page,
            int size
    ) {
        int offset = page * size;

        List<YuanTransaction> transactions = yuanTransactionMapper.findWithFilters(userId, startDate, endDate, offset, size);

        List<AdminYuanTransactionDTO> dtos = transactions.stream().map(transaction -> {
            AdminYuanTransactionDTO dto = new AdminYuanTransactionDTO();
            dto.setId(transaction.getId());
            dto.setUserId(transaction.getUserId());
            dto.setAmount(transaction.getAmount());
            dto.setDescription(transaction.getDescription());
            dto.setCreatedAt(transaction.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());

        long totalElements = yuanTransactionMapper.countWithFilters(userId, startDate, endDate);

        return new PageResponseDTO<>(dtos, totalElements, page, size);
    }
}