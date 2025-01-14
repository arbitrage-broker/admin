package com.arbitragebroker.admin.filter;

import com.arbitragebroker.admin.enums.CurrencyType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Setter
@ToString
@Accessors(chain = true)
public class ArbitrageFilter {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateFrom;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateTo;
    private UUID userId;
    private Long exchangeId;
    private Long coinId;
    private Long subscriptionId;
    private BigDecimal rewardFrom;
    private BigDecimal rewardTo;
    private CurrencyType currency;

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Optional<LocalDateTime> getCreatedDateFrom() {
        return Optional.ofNullable(createdDateFrom);
    }

    public void setCreatedDateFrom(LocalDateTime createdDateFrom) {
        this.createdDateFrom = createdDateFrom;
    }

    public Optional<LocalDateTime> getCreatedDateTo() {
        return Optional.ofNullable(createdDateTo);
    }

    public void setCreatedDateTo(LocalDateTime createdDateTo) {
        this.createdDateTo = createdDateTo;
    }

    public Optional<UUID> getUserId() {
        return Optional.ofNullable(userId);
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Optional<Long> getExchangeId() {
        return Optional.ofNullable(exchangeId);
    }

    public void setExchangeId(Long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Optional<Long> getCoinId() {
        return Optional.ofNullable(coinId);
    }

    public void setCoinId(Long coinId) {
        this.coinId = coinId;
    }

    public Optional<Long> getSubscriptionId() {
        return Optional.ofNullable(subscriptionId);
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Optional<BigDecimal> getRewardFrom() {
        return Optional.ofNullable(rewardFrom);
    }

    public Optional<BigDecimal> getRewardTo() {
        return Optional.ofNullable(rewardTo);
    }

    public Optional<CurrencyType> getCurrency() {
        return Optional.ofNullable(currency);
    }
}
