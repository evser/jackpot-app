package com.jackpot.service.reward;

import com.jackpot.entity.Jackpot;
import com.jackpot.entity.RewardConfigType;

/**
 * Interface for the Strategy Pattern, defining reward logic.
 */
public interface RewardStrategy {
    /**
     * Checks if the bet won the jackpot.
     *
     * @param jackpot The current jackpot (after contribution)
     * @return true if the bet won, false otherwise
     */
    boolean checkWin(Jackpot jackpot);

    /**
     * @return The type of strategy
     */
    RewardConfigType getType();
}