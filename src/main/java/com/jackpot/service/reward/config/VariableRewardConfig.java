package com.jackpot.service.reward.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableRewardConfig {
    /**
     * Base chance (e.g., 0.001 for 0.1%).
     */
    private double baseChance;
    /**
     * Chance increase rate (e.g., 0.0000001).
     */
    private double increaseRate;
    /**
     * Pool limit at which the chance becomes 100% (e.g., 1000000).
     */
    private BigDecimal poolLimit;
}
