package com.jackpot.service;

import com.jackpot.dto.RewardResponse;
import com.jackpot.entity.Jackpot;
import com.jackpot.entity.JackpotContribution;
import com.jackpot.entity.JackpotReward;
import com.jackpot.kafka.message.JackpotBetMessage;
import com.jackpot.repository.JackpotContributionRepository;
import com.jackpot.repository.JackpotRepository;
import com.jackpot.repository.JackpotRewardRepository;
import com.jackpot.service.contribuition.ContributionStrategy;
import com.jackpot.service.contribuition.ContributionStrategyFactory;
import com.jackpot.service.reward.RewardStrategy;
import com.jackpot.service.reward.RewardStrategyFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class JackpotService {

    private final JackpotRepository jackpotRepository;
    private final JackpotContributionRepository contributionRepository;
    private final JackpotRewardRepository rewardRepository;
    private final ContributionStrategyFactory contributionFactory;
    private final RewardStrategyFactory rewardFactory;

    /**
     * Processes a bet contribution. This method is called
     * by the Kafka consumer (or mock producer).
     *
     * @param message The bet message from Kafka
     */
    @Transactional
    public void processContribution(JackpotBetMessage message) {
        if (contributionRepository.findByBetId(message.getBetId()).isPresent()) {
            log.warn("Duplicate processing detected for BetId: {}. Ignoring.", message.getBetId());
            return;
        }

        // Find and lock the jackpot to prevent race conditions
        Jackpot jackpot = jackpotRepository.findWithLockById(message.getJackpotId())
                .orElseThrow(() -> new EntityNotFoundException("Jackpot not found: " + message.getJackpotId()));

        ContributionStrategy strategy = contributionFactory.getStrategy(jackpot.getContributionConfigType());

        BigDecimal contributionAmount = strategy.calculateContribution(jackpot, message.getBetAmount());

        BigDecimal newPoolValue = jackpot.getCurrentPoolValue().add(contributionAmount)
                .setScale(4, RoundingMode.HALF_UP);
        jackpot.setCurrentPoolValue(newPoolValue);
        jackpotRepository.save(jackpot);

        JackpotContribution contribution = new JackpotContribution();
        contribution.setBetId(message.getBetId());
        contribution.setUserId(message.getUserId());
        contribution.setJackpotId(message.getJackpotId());
        contribution.setStakeAmount(message.getBetAmount());
        contribution.setContributionAmount(contributionAmount);
        contribution.setCurrentJackpotAmount(newPoolValue);
        contributionRepository.save(contribution);

        log.info("Processed contribution for BetId: {}. Added {} to Jackpot: {}. New Pool: {}",
                message.getBetId(), contributionAmount, jackpot.getId(), newPoolValue);
    }

    /**
     * Evaluates a bet for a jackpot win. This method is called
     * by the API controller.
     *
     * @param betId The ID of the bet to evaluate
     * @return RewardResponse with win information
     */
    @Transactional
    public RewardResponse evaluateBet(Long betId) {
        JackpotContribution contribution = contributionRepository.findByBetId(betId)
                .orElseThrow(() -> new EntityNotFoundException("Contribution not found for BetId: " + betId + ". It might not be processed yet."));

        Long jackpotId = contribution.getJackpotId();
        Jackpot jackpot = jackpotRepository.findWithLockById(jackpotId)
                .orElseThrow(() -> new EntityNotFoundException("Jackpot not found: " + jackpotId));

        RewardStrategy strategy = rewardFactory.getStrategy(jackpot.getRewardConfigType());

        JackpotReward reward;
        reward = rewardRepository.findByBetId(betId);
        if (reward != null) {
            return new RewardResponse(reward.getJackpotRewardAmount());
        } else {
            reward = new JackpotReward();
            reward.setBetId(betId);
            reward.setUserId(contribution.getUserId());
            reward.setJackpotId(jackpotId);
        }

        BigDecimal rewardAmount;
        boolean isJackpot = strategy.checkWin(jackpot);
        if (isJackpot) {
            rewardAmount = jackpot.getCurrentPoolValue().setScale(4, RoundingMode.HALF_UP);
            log.info("JACKPOT! BetId: {} won {} from Jackpot: {}", betId, rewardAmount, jackpotId);
            jackpot.setCurrentPoolValue(jackpot.getInitialPoolValue());
            jackpotRepository.save(jackpot);
        } else {
            log.info("No win for BetId: {}. Jackpot: {} pool remains: {}", betId, jackpotId, jackpot.getCurrentPoolValue());
            rewardAmount = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        reward.setJackpotRewardAmount(rewardAmount);
        rewardRepository.save(reward);
        return new RewardResponse(rewardAmount);
    }
}