package com.jackpot.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RewardResponse {
    private Boolean isJackpot;
    private BigDecimal rewardAmount;

    public RewardResponse(BigDecimal rewardAmount) {
        this.isJackpot = BigDecimal.ZERO.compareTo(rewardAmount) < 0;
        this.rewardAmount = rewardAmount;
    }
}
