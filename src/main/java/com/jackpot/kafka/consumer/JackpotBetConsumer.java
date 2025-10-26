package com.jackpot.kafka.consumer;

import com.jackpot.kafka.message.JackpotBetMessage;
import com.jackpot.service.JackpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("!mock-kafka")
@RequiredArgsConstructor
public class JackpotBetConsumer {

    private final JackpotService jackpotService;

    /**
     * Listens to the 'jackpot-bets' Kafka topic.
     *
     * @param message Deserialized bet message
     */
    @KafkaListener(topics = "${jackpot.kafka.topic}")
    public void handleBet(@Payload JackpotBetMessage message) {
        log.info("Received bet from Kafka. BetId: {}", message.getBetId());
        try {
            jackpotService.processContribution(message);
        } catch (Exception e) {
            log.error("Error processing received bet. BetId: {}", message.getBetId(), e);
            // In the future we can implement error handling, e.g., Dead Letter Topic
        }
    }
}
