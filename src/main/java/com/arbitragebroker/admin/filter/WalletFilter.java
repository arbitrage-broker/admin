package com.arbitragebroker.admin.filter;

import com.arbitragebroker.admin.enums.CurrencyType;
import com.arbitragebroker.admin.enums.EntityStatusType;
import com.arbitragebroker.admin.enums.NetworkType;
import com.arbitragebroker.admin.enums.TransactionType;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Setter
@ToString
@Accessors(chain = true)
public class WalletFilter {
    private Long id;
    private BigDecimal amount;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private BigDecimal actualAmount;
    private BigDecimal actualAmountFrom;
    private BigDecimal actualAmountTo;
    private TransactionType transactionType;
    private String transactionHash;
    private CurrencyType currency;
    private NetworkType network;
    private UUID userId;
    private EntityStatusType status;
    private String address;
    private Long roleId;

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }
    public Optional<BigDecimal> getAmount() {
        return Optional.ofNullable(amount);
    }
    public Optional<BigDecimal> getAmountFrom() {
        return Optional.ofNullable(amountFrom);
    }
    public Optional<BigDecimal> getAmountTo() {
        return Optional.ofNullable(amountTo);
    }
    public Optional<BigDecimal> getActualAmount() {
        return Optional.ofNullable(actualAmount);
    }
    public Optional<BigDecimal> getActualAmountFrom() {
        return Optional.ofNullable(actualAmountFrom);
    }
    public Optional<BigDecimal> getActualAmountTo() {
        return Optional.ofNullable(actualAmountTo);
    }
    public Optional<TransactionType> getTransactionType() {
        return Optional.ofNullable(transactionType);
    }

    public Optional<String> getTransactionHash() {
        if (transactionHash == null || transactionHash.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(transactionHash);
    }

    public Optional<CurrencyType> getCurrency() {
        return Optional.ofNullable(currency);
    }
    public Optional<NetworkType> getNetwork() {return Optional.ofNullable(network);}
    public Optional<UUID> getUserId() {
        return Optional.ofNullable(userId);
    }

    public Optional<EntityStatusType> getStatus() {
        return Optional.ofNullable(status);
    }
    public Optional<Long> getRoleId() {
        return Optional.ofNullable(roleId);
    }

    public Optional<String> getAddress() {
        if (address == null || address.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(address);
    }
}
