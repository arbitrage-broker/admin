package com.arbitragebroker.admin.strategy;

import com.arbitragebroker.admin.dto.TransactionResponse;
import com.arbitragebroker.admin.model.WalletModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface NetworkStrategy {
    BigDecimal getPrice(String token);
    TransactionResponse getTransactionInfo(String hash);
    default boolean validate(WalletModel model) {
        try {
            var transaction = getTransactionInfo(model.getTransactionHash());
            if (transaction == null)
                return false;
            if (!transaction.isSuccess())
                return false;
            boolean amountIsValid = false;
            if (transaction.getAmount().compareTo(model.getActualAmount().setScale(4, RoundingMode.HALF_UP)) == 0
                    || transaction.getAmount().subtract(model.getActualAmount().setScale(4, RoundingMode.HALF_UP)).abs().compareTo(new BigDecimal("0.0001")) == 0)
                amountIsValid = true;

            if(!amountIsValid)
                return false;
            if (!transaction.getToAddress().equals(model.getAddress()))
                return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
