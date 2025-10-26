package com.jackpot;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("!mock-kafka")
public class KafkaConfig {

    @Value("${jackpot.kafka.topic}")
    private String topicName;

    /**
     * Creates the 'jackpot-bets' topic on application startup if it doesn't exist.
     *
     * @return NewTopic
     */
    @Bean
    public NewTopic jackpotBetsTopic() {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1) // In a real production environment, this will be > 1
                .build();
    }

}
