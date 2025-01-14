package com.arbitragebroker.admin.strategy.impl;


import com.arbitragebroker.admin.model.WalletModel;
import com.arbitragebroker.admin.strategy.TransactionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BonusStrategyImpl implements TransactionStrategy {

    @Override
    public void beforeSave(WalletModel model) {

    }

    @Override
    public void afterSave(WalletModel model) {

    }
}
