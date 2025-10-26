package com.jackpot.service.reward;

import com.jackpot.entity.RewardConfigType;
import org.springframework.stereotype.Component;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Factory to get the correct RewardStrategy implementation by type.
 */
@Component
public class RewardStrategyFactory {
    private final Map<RewardConfigType, RewardStrategy> strategies;

    public RewardStrategyFactory(List<RewardStrategy> strategyList) {
        strategies = new EnumMap<>(RewardConfigType.class);
        strategyList.forEach(strategy -> strategies.put(strategy.getType(), strategy));
    }

    public RewardStrategy getStrategy(RewardConfigType type) {
        RewardStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for reward type: " + type);
        }
        return strategy;
    }
}