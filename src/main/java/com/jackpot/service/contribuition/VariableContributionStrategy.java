package com.jackpot.service.contribuition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackpot.entity.ContributionConfigType;
import com.jackpot.entity.Jackpot;
import com.jackpot.service.contribuition.config.VariableContributionConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
@RequiredArgsConstructor
public class VariableContributionStrategy implements ContributionStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public BigDecimal calculateContribution(Jackpot jackpot, BigDecimal betAmount) {
        try {
            VariableContributionConfig config = objectMapper.readValue(
                    jackpot.getContributionConfigJson(),
                    VariableContributionConfig.class
            );

            // Logic: Contribution % decreases as the pool grows
            // effectivePercentage = initial% - (currentPool * decayRate)
            double poolValue = jackpot.getCurrentPoolValue().doubleValue();
            double effectivePercentage = config.getInitialPercentage() - (poolValue * config.getDecayRate());

            // Clamp the percentage to the minimum
            double clampedPercentage = Math.max(config.getMinPercentage(), effectivePercentage);

            return betAmount.multiply(BigDecimal.valueOf(clampedPercentage))
                    .setScale(4, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("Error parsing VariableContributionConfig for jackpotId: {}", jackpot.getId(), e);
            throw new IllegalStateException("Invalid contribution config JSON", e);
        }
    }

    @Override
    public ContributionConfigType getType() {
        return ContributionConfigType.VARIABLE;
    }
}
