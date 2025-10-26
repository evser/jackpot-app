package com.jackpot.kafka.producer;

import com.jackpot.kafka.message.JackpotBetMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("!mock-kafka")
@RequiredArgsConstructor
public class KafkaBetProducer implements BetProducer {

    private final KafkaTemplate<String, JackpotBetMessage> kafkaTemplate;

    @Value("${jackpot.kafka.topic}")
    private String topicName;

    @Override
    public void publish(JackpotBetMessage betMessage) {
        try {
            log.info("Publishing bet to Kafka. BetId: {}", betMessage.getBetId());
            // Use jackpotId as the key so bets for the same jackpot go to the same partition
            kafkaTemplate.send(topicName, String.valueOf(betMessage.getJackpotId()), betMessage);
        } catch (Exception e) {
            log.error("Error publishing bet to Kafka. BetId: {}", betMessage.getBetId(), e);
            // Note: here we can in the future implement retry or a dead-letter-queue logic
        }
    }
}
