package com.jackpot.kafka.producer;

import com.jackpot.kafka.message.JackpotBetMessage;

public interface BetProducer {
    /**
     * Publishes a bet message.
     *
     * @param betMessage The bet message
     */
    void publish(JackpotBetMessage betMessage);
}
