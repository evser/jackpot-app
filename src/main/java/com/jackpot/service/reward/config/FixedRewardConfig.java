package com.jackpot.service.reward.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for parsing JSON configuration of type FIXED_CHANCE.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedRewardConfig {
    /**
     * Win chance (e.g., 0.01 for 1%).
     */
    private double winChance;
}