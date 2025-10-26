package com.jackpot.kafka.producer;

import com.jackpot.kafka.message.JackpotBetMessage;
import com.jackpot.service.JackpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("mock-kafka")
@RequiredArgsConstructor
public class MockLoggingBetProducer implements BetProducer {

    private final JackpotService jackpotService;

    @Override
    public void publish(JackpotBetMessage betMessage) {
        log.info("[MOCK-KAFKA] Logging bet data: {}", betMessage);
        try {
            // Direct call to the processing logic, as if received from Kafka
            jackpotService.processContribution(betMessage);
            log.info("[MOCK-KAFKA] Bet processed synchronously. BetId: {}", betMessage.getBetId());
        } catch (Exception e) {
            log.error("[MOCK-KAFKA] Error processing 'mock' bet. BetId: {}", betMessage.getBetId(), e);
        }
    }
}
