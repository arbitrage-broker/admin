package com.arbitragebroker.admin.strategy;

import com.arbitragebroker.admin.model.WalletModel;

public interface TransactionStrategy {
    void beforeSave(WalletModel model);
    void afterSave(WalletModel newModedl,WalletModel oldModel);
}
