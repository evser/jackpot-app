package com.jackpot.repository;

import com.jackpot.entity.JackpotReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JackpotRewardRepository extends JpaRepository<JackpotReward, Long> {

    JackpotReward findByBetId(Long betId);
}
