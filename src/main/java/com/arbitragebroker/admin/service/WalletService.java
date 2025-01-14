package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.enums.TransactionType;
import com.arbitragebroker.admin.filter.WalletFilter;
import com.arbitragebroker.admin.model.WalletModel;

import java.math.BigDecimal;
import java.util.Map;

public interface WalletService extends BaseService<WalletFilter, WalletModel, Long> {
    WalletModel createFromAdmin(WalletModel model);
    BigDecimal totalBalance();
    BigDecimal totalDeposit();
    BigDecimal totalWithdrawal();
    BigDecimal totalBonus();
    BigDecimal totalReward();
//    List<BalanceModel> totalProfit();
    Map<Long, BigDecimal> findAllWithinDateRange(long startDate, long endDate, TransactionType transactionType);
    BigDecimal referralDepositBonus(BigDecimal amount);
}
