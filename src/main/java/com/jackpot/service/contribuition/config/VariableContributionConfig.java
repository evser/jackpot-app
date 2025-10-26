package com.jackpot.service.contribuition.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for parsing JSON configuration of type VARIABLE.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableContributionConfig {
    /**
     * Initial percentage (e.g., 0.1 for 10%).
     */
    private double initialPercentage;
    /**
     * Percentage decay rate (e.g., 0.000001).
     */
    private double decayRate;
    /**
     * Minimum percentage (e.g., 0.01 for 1%).
     */
    private double minPercentage;
}