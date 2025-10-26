package com.jackpot.service.reward;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackpot.entity.Jackpot;
import com.jackpot.entity.RewardConfigType;
import com.jackpot.service.reward.config.VariableRewardConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class VariableRewardStrategy implements RewardStrategy {

    private final ObjectMapper objectMapper;

    /**
     * Win chance increases with the pool
     * `effectiveChance = baseChance + (currentPool * increaseRate)`
     * <p>
     * Also, if the pool hit the limit, it's a 100% win.
     *
     * @param jackpot The current jackpot (after contribution)
     */
    @Override
    public boolean checkWin(Jackpot jackpot) {
        try {
            VariableRewardConfig config = objectMapper.readValue(
                    jackpot.getRewardConfigJson(),
                    VariableRewardConfig.class
            );

            BigDecimal currentPool = jackpot.getCurrentPoolValue();
            if (currentPool.compareTo(config.getPoolLimit()) >= 0) {
                return true;
            }

            double effectiveChance = config.getBaseChance() + (currentPool.doubleValue() * config.getIncreaseRate());
            return Math.random() < effectiveChance;
        } catch (Exception e) {
            log.error("Error parsing VariableChanceRewardConfig for jackpotId: {}", jackpot.getId(), e);
            throw new IllegalStateException("Invalid reward config JSON", e);
        }
    }

    @Override
    public RewardConfigType getType() {
        return RewardConfigType.VARIABLE;
    }
}
