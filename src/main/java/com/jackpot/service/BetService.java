package com.jackpot.service;

import com.jackpot.dto.BetRequest;
import com.jackpot.kafka.message.JackpotBetMessage;
import com.jackpot.kafka.producer.BetProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BetService {

    private final BetProducer betProducer;

    public void publish(BetRequest betRequest) {
        JackpotBetMessage message = JackpotBetMessage.from(betRequest);
        betProducer.publish(message);
    }
}