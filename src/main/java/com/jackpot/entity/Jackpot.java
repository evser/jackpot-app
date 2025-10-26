package com.jackpot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "jackpots")
public class Jackpot {
    @Id
    private Long id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal initialPoolValue;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentPoolValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContributionConfigType contributionConfigType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String contributionConfigJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardConfigType rewardConfigType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String rewardConfigJson;
}