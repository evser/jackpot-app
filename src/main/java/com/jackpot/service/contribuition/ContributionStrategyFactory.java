package com.jackpot.service.contribuition;

import com.jackpot.entity.ContributionConfigType;
import org.springframework.stereotype.Component;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Factory to get the correct ContributionStrategy implementation by type.
 */
@Component
public class ContributionStrategyFactory {
    private final Map<ContributionConfigType, ContributionStrategy> strategies;

    /**
     * Spring injects a list of all beans that implement ContributionStrategy,
     * and we store them in a Map for quick access.
     *
     * @param strategyList List of all ContributionStrategy implementations
     */
    public ContributionStrategyFactory(List<ContributionStrategy> strategyList) {
        strategies = new EnumMap<>(ContributionConfigType.class);
        strategyList.forEach(strategy -> strategies.put(strategy.getType(), strategy));
    }

    public ContributionStrategy getStrategy(ContributionConfigType type) {
        ContributionStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for contribution type: " + type);
        }
        return strategy;
    }
}