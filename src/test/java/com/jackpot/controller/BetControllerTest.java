package com.jackpot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackpot.dto.BetRequest;
import com.jackpot.dto.RewardResponse;
import com.jackpot.service.BetService;
import com.jackpot.service.JackpotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BetController.class)
class BetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BetService betService;

    @MockBean
    private JackpotService jackpotService;

    @Test
    void shouldPublishBetAndReturnAccepted() throws Exception {
        BetRequest betRequest = new BetRequest(123L, 456L, 1L, new BigDecimal("10.00"));
        doNothing().when(betService).publish(any());

        mockMvc.perform(post("/api/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(betRequest)))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldReturnBadRequestWhenBetRequestIsInvalid() throws Exception {
        BetRequest invalidRequest = new BetRequest(null, 456L, 1L, new BigDecimal("10.00"));

        mockMvc.perform(post("/api/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldEvaluateBetAndReturnRewardResponse() throws Exception {
        Long betId = 123L;
        RewardResponse rewardResponse = new RewardResponse(new BigDecimal("1000.00"));

        when(jackpotService.evaluateBet(betId)).thenReturn(rewardResponse);

        mockMvc.perform(post("/api/bets/{betId}/evaluate", betId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isJackpot").value(true))
                .andExpect(jsonPath("$.rewardAmount").value(1000.00));
    }


    @Test
    void shouldEvaluateBetAndReturnNoRewardResponse() throws Exception {
        Long betId = 123L;
        RewardResponse rewardResponse = new RewardResponse(BigDecimal.ZERO);

        when(jackpotService.evaluateBet(betId)).thenReturn(rewardResponse);

        mockMvc.perform(post("/api/bets/{betId}/evaluate", betId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isJackpot").value(false))
                .andExpect(jsonPath("$.rewardAmount").value(0.00));
    }
}

