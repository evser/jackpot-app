package com.jackpot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackpot.dto.RewardResponse;
import com.jackpot.entity.ContributionConfigType;
import com.jackpot.entity.Jackpot;
import com.jackpot.entity.JackpotContribution;
import com.jackpot.entity.JackpotReward;
import com.jackpot.entity.RewardConfigType;
import com.jackpot.kafka.message.JackpotBetMessage;
import com.jackpot.repository.JackpotContributionRepository;
import com.jackpot.repository.JackpotRepository;
import com.jackpot.repository.JackpotRewardRepository;
import com.jackpot.service.contribuition.ContributionStrategy;
import com.jackpot.service.contribuition.ContributionStrategyFactory;
import com.jackpot.service.contribuition.config.FixedContributionConfig;
import com.jackpot.service.reward.RewardStrategy;
import com.jackpot.service.reward.RewardStrategyFactory;
import com.jackpot.service.reward.config.FixedRewardConfig;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JackpotServiceTest {

    @Mock
    private JackpotRepository jackpotRepository;
    @Mock
    private JackpotContributionRepository jackpotContributionRepository;
    @Mock
    private JackpotRewardRepository jackpotRewardRepository;
    @Mock
    private ContributionStrategyFactory contributionStrategyFactory;
    @Mock
    private RewardStrategyFactory rewardStrategyFactory;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private JackpotService jackpotService;

    @Mock
    private ContributionStrategy mockContributionStrategy;
    @Mock
    private RewardStrategy mockRewardStrategy;

    private Jackpot testJackpot;
    private JackpotBetMessage testBetMessage;

    private static final Long TEST_JACKPOT_ID = 1L;
    private static final Long TEST_BET_ID = 123L;
    private static final Long TEST_USER_ID = 456L;


    @BeforeEach
    void setUp() {
        // Setup a test jackpot
        FixedContributionConfig contribConfig = new FixedContributionConfig(0.1); // 10%
        FixedRewardConfig rewardConfig = new FixedRewardConfig(0.05); // 5%

        testJackpot = new Jackpot();
        testJackpot.setId(TEST_JACKPOT_ID);
        testJackpot.setInitialPoolValue(new BigDecimal("1000"));
        testJackpot.setCurrentPoolValue(new BigDecimal("5000"));
        testJackpot.setContributionConfigType(ContributionConfigType.FIXED);
        testJackpot.setContributionConfigJson(toJson(contribConfig));
        testJackpot.setRewardConfigType(RewardConfigType.FIXED);
        testJackpot.setRewardConfigJson(toJson(rewardConfig));

        // Setup a test bet message with Long IDs
        testBetMessage = new JackpotBetMessage(TEST_BET_ID, TEST_USER_ID, TEST_JACKPOT_ID, new BigDecimal("100.00"));
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldContributeToJackpotUsingFixedStrategy() {
        // Arrange
        BigDecimal contribution = new BigDecimal("10.00"); // 10% of 100
        when(jackpotRepository.findWithLockById(TEST_JACKPOT_ID)).thenReturn(Optional.of(testJackpot));
        when(contributionStrategyFactory.getStrategy(ContributionConfigType.FIXED)).thenReturn(mockContributionStrategy);
        when(mockContributionStrategy.calculateContribution(any(Jackpot.class), any(BigDecimal.class)))
                .thenReturn(contribution);

        ArgumentCaptor<Jackpot> jackpotCaptor = ArgumentCaptor.forClass(Jackpot.class);
        ArgumentCaptor<JackpotContribution> contributionCaptor = ArgumentCaptor.forClass(JackpotContribution.class);

        // Act
        jackpotService.processContribution(testBetMessage);

        // Assert
        verify(jackpotRepository).save(jackpotCaptor.capture());
        Jackpot savedJackpot = jackpotCaptor.getValue();
        assertThat(savedJackpot.getCurrentPoolValue()).isEqualByComparingTo("5010.00");

        verify(jackpotContributionRepository).save(contributionCaptor.capture());
        JackpotContribution savedContribution = contributionCaptor.getValue();
        assertThat(savedContribution.getBetId()).isEqualTo(TEST_BET_ID);
        assertThat(savedContribution.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(savedContribution.getContributionAmount()).isEqualByComparingTo("10.00");
        assertThat(savedContribution.getCurrentJackpotAmount()).isEqualByComparingTo("5010.00");
    }

    @Test
    void shouldEvaluateBetAndWinAndResetJackpot() {
        // Arrange
        JackpotContribution contribution = new JackpotContribution();
        contribution.setBetId(TEST_BET_ID);
        contribution.setUserId(TEST_USER_ID);
        contribution.setJackpotId(TEST_JACKPOT_ID);
        contribution.setContributionAmount(new BigDecimal("10.00"));
        contribution.setCurrentJackpotAmount(new BigDecimal("5010.00"));
        contribution.setStakeAmount(new BigDecimal("100.00"));

        when(jackpotContributionRepository.findByBetId(TEST_BET_ID)).thenReturn(Optional.of(contribution));

        testJackpot.setCurrentPoolValue(new BigDecimal("5010.00"));

        when(jackpotRepository.findWithLockById(TEST_JACKPOT_ID)).thenReturn(Optional.of(testJackpot));
        when(rewardStrategyFactory.getStrategy(RewardConfigType.FIXED)).thenReturn(mockRewardStrategy);
        when(mockRewardStrategy.checkWin(any(Jackpot.class))).thenReturn(true);

        ArgumentCaptor<Jackpot> jackpotCaptor = ArgumentCaptor.forClass(Jackpot.class);
        ArgumentCaptor<JackpotReward> rewardCaptor = ArgumentCaptor.forClass(JackpotReward.class);

        // Act
        RewardResponse response = jackpotService.evaluateBet(TEST_BET_ID);

        // Assert
        assertThat(response.getIsJackpot()).isTrue();
        assertThat(response.getRewardAmount()).isEqualByComparingTo("5010.00"); // This was the pool before reset

        verify(jackpotRepository).save(jackpotCaptor.capture());
        Jackpot savedJackpot = jackpotCaptor.getValue();
        // Verifies pool was reset to initial value
        assertThat(savedJackpot.getCurrentPoolValue()).isEqualByComparingTo(testJackpot.getInitialPoolValue());

        verify(jackpotRewardRepository).save(rewardCaptor.capture());
        JackpotReward savedReward = rewardCaptor.getValue();
        assertThat(savedReward.getBetId()).isEqualTo(TEST_BET_ID);
        assertThat(savedReward.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(savedReward.getJackpotId()).isEqualTo(TEST_JACKPOT_ID);
        assertThat(savedReward.getJackpotRewardAmount()).isEqualByComparingTo("5010.00");
    }

    @Test
    void shouldEvaluateAndSaveBetWhenNoWin() {
        // Arrange
        JackpotContribution contribution = new JackpotContribution();
        contribution.setJackpotId(TEST_JACKPOT_ID);
        contribution.setBetId(TEST_BET_ID);

        when(jackpotContributionRepository.findByBetId(TEST_BET_ID)).thenReturn(Optional.of(contribution));
        when(jackpotRepository.findWithLockById(TEST_JACKPOT_ID)).thenReturn(Optional.of(testJackpot));
        when(rewardStrategyFactory.getStrategy(RewardConfigType.FIXED)).thenReturn(mockRewardStrategy);
        when(mockRewardStrategy.checkWin(any(Jackpot.class))).thenReturn(false);

        // Act
        RewardResponse response = jackpotService.evaluateBet(TEST_BET_ID);

        // Assert
        assertThat(response.getIsJackpot()).isFalse();
        assertThat(response.getRewardAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(jackpotRewardRepository).save(any());
        verify(jackpotRepository, never()).save(any());
    }

    @Test
    void shouldReturnBetNotFoundResponseWhenEvaluating() {
        // Arrange
        Long betId = 999L;
        when(jackpotContributionRepository.findByBetId(betId)).thenReturn(Optional.empty());

        // Act
        assertThatThrownBy(() -> jackpotService.evaluateBet(betId)).isInstanceOf(EntityNotFoundException.class);
        verify(jackpotRepository, never()).findWithLockById(anyLong());
    }
}

