package com.jackpot.controller;

import com.jackpot.dto.BetRequest;
import com.jackpot.dto.RewardResponse;
import com.jackpot.service.BetService;
import com.jackpot.service.JackpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bets")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;
    private final JackpotService jackpotService;

    @PostMapping
    public ResponseEntity<Void> publishBet(@Valid @RequestBody BetRequest request) {
        betService.publish(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{betId}/evaluate")
    public RewardResponse evaluateBet(@PathVariable Long betId) {
        return jackpotService.evaluateBet(betId);
    }
}