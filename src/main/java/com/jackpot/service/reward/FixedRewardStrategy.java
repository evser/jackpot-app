package com.jackpot.service.reward;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackpot.entity.Jackpot;
import com.jackpot.entity.RewardConfigType;
import com.jackpot.service.reward.config.FixedRewardConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FixedRewardStrategy implements RewardStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public boolean checkWin(Jackpot jackpot) {
        try {
            FixedRewardConfig config = objectMapper.readValue(
                    jackpot.getRewardConfigJson(),
                    FixedRewardConfig.class
            );
            return Math.random() < config.getWinChance();
        } catch (Exception e) {
            log.error("Error parsing FixedChanceRewardConfig for jackpotId: {}", jackpot.getId(), e);
            throw new IllegalStateException("Invalid reward config JSON", e);
        }
    }

    @Override
    public RewardConfigType getType() {
        return RewardConfigType.FIXED;
    }
}
