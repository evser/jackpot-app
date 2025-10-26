package com.jackpot.service.contribuition;

import com.jackpot.entity.ContributionConfigType;
import com.jackpot.entity.Jackpot;
import java.math.BigDecimal;

/**
 * Interface for the Strategy Pattern, defining contribution logic.
 */
public interface ContributionStrategy {
    /**
     * Calculates the contribution amount.
     *
     * @param jackpot   The current jackpot
     * @param betAmount The bet amount
     * @return The contribution amount
     */
    BigDecimal calculateContribution(Jackpot jackpot, BigDecimal betAmount);

    /**
     * @return The type of strategy
     */
    ContributionConfigType getType();
}
