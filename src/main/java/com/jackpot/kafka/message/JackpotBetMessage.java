package com.jackpot.kafka.message;

import com.jackpot.dto.BetRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Message for the Kafka topic.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JackpotBetMessage {
    private Long betId;
    private Long userId;
    private Long jackpotId;
    private BigDecimal betAmount;

    public static JackpotBetMessage from(BetRequest request) {
        return new JackpotBetMessage(
                request.getBetId(),
                request.getUserId(),
                request.getJackpotId(),
                request.getBetAmount()
        );
    }
}