package com.jackpot.service.contribuition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackpot.entity.ContributionConfigType;
import com.jackpot.entity.Jackpot;
import com.jackpot.service.contribuition.config.FixedContributionConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
@RequiredArgsConstructor
public class FixedContributionStrategy implements ContributionStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public BigDecimal calculateContribution(Jackpot jackpot, BigDecimal betAmount) {
        try {
            FixedContributionConfig config = objectMapper.readValue(
                    jackpot.getContributionConfigJson(),
                    FixedContributionConfig.class
            );
            return betAmount.multiply(BigDecimal.valueOf(config.getPercentage()))
                    .setScale(4, RoundingMode.HALF_UP);
        } catch (Exception ex) {
            log.error("Error parsing FixedContributionConfig for jackpotId: {}", jackpot.getId(), ex);
            throw new IllegalStateException("Invalid contribution config JSON", ex);
        }
    }

    @Override
    public ContributionConfigType getType() {
        return ContributionConfigType.FIXED;
    }
}