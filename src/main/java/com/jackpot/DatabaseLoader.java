package com.jackpot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackpot.entity.ContributionConfigType;
import com.jackpot.entity.Jackpot;
import com.jackpot.entity.RewardConfigType;
import com.jackpot.repository.JackpotRepository;
import com.jackpot.service.contribuition.config.FixedContributionConfig;
import com.jackpot.service.contribuition.config.VariableContributionConfig;
import com.jackpot.service.reward.config.FixedRewardConfig;
import com.jackpot.service.reward.config.VariableRewardConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Loads initial data (Jackpots) into the H2 database on startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseLoader implements CommandLineRunner {

    private final JackpotRepository jackpotRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("Loading initial jackpots into the database...");

        // --- FIRST JACKPOT ---
        // Contribution: Fixed 5%
        // Reward: Variable chance, 100% win if pool hits 1000000
        if (jackpotRepository.findById(1L).isEmpty()) {
            Jackpot jackpot1 = new Jackpot();
            jackpot1.setId(1L);
            jackpot1.setInitialPoolValue(new BigDecimal("10000.00"));
            jackpot1.setCurrentPoolValue(new BigDecimal("10000.00"));

            // Configs
            jackpot1.setContributionConfigType(ContributionConfigType.FIXED);
            jackpot1.setContributionConfigJson(objectMapper.writeValueAsString(
                    new FixedContributionConfig(0.05) // 5%
            ));

            jackpot1.setRewardConfigType(RewardConfigType.VARIABLE);
            jackpot1.setRewardConfigJson(objectMapper.writeValueAsString(
                    new VariableRewardConfig(
                            0.001, // 0.1% base chance
                            0.000001, // Chance increases with pool
                            new BigDecimal("1000000") // 100% chance at 1M
                    )
            ));
            jackpotRepository.save(jackpot1);
            log.info("Created first jackpot.");
        }

        // --- SECOND JACKPOT ---
        // Contribution: Variable 10% (decays to 2%)
        // Reward: Fixed 1% chance
        if (jackpotRepository.findById(2L).isEmpty()) {
            Jackpot second = new Jackpot();
            second.setId(2L);
            second.setInitialPoolValue(new BigDecimal("1000.00"));
            second.setCurrentPoolValue(new BigDecimal("1000.00"));

            // Configs
            second.setContributionConfigType(ContributionConfigType.VARIABLE);
            second.setContributionConfigJson(objectMapper.writeValueAsString(
                    new VariableContributionConfig(
                            0.10, // initial 10%
                            0.00001, // decay rate
                            0.02 // minimum contribution 2%
                    )
            ));

            second.setRewardConfigType(RewardConfigType.FIXED);
            second.setRewardConfigJson(objectMapper.writeValueAsString(
                    new FixedRewardConfig(0.01) // 1%
            ));
            jackpotRepository.save(second);
            log.info("Created second jackpot.");
        }
    }
}