package com.jackpot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetRequest {
    @NotNull(message = "betId cannot be null")
    private Long betId;

    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotNull(message = "jackpotId cannot be null")
    private Long jackpotId;

    @NotNull(message = "betAmount cannot be null")
    @Positive(message = "betAmount must be positive")
    private BigDecimal betAmount;
}
