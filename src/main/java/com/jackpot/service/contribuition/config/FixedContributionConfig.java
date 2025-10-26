package com.jackpot.service.contribuition.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for parsing JSON configuration of type FIXED.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedContributionConfig {
    /**
     * Percentage as a decimal (e.g., 0.05 for 5%).
     */
    private double percentage;
}
