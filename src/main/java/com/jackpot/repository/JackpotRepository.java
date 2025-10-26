package com.jackpot.repository;

import com.jackpot.entity.Jackpot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JackpotRepository extends JpaRepository<Jackpot, Long> {

    /**
     * Finds a jackpot by ID and applies a PESSIMISTIC_WRITE lock
     * to prevent race conditions when multiple bets try to contribute to
     * or win the same jackpot simultaneously.
     *
     * @param id The Jackpot ID
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Jackpot> findWithLockById(Long id);
}
