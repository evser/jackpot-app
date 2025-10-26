package com.jackpot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "jackpot_contributions")
public class JackpotContribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long betId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long jackpotId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal stakeAmount; // The Bet Amount

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal contributionAmount; // The contribution amount

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentJackpotAmount; // The pool *after* this contribution

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
