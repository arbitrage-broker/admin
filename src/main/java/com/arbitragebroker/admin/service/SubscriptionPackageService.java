package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.filter.SubscriptionPackageFilter;
import com.arbitragebroker.admin.model.SubscriptionPackageModel;

import java.math.BigDecimal;

public interface SubscriptionPackageService extends BaseService<SubscriptionPackageFilter, SubscriptionPackageModel, Long> {
    SubscriptionPackageModel findMatchedPackageByAmount(BigDecimal amount);
}
