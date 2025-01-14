package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.filter.SubscriptionFilter;
import com.arbitragebroker.admin.model.SubscriptionModel;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService extends BaseService<SubscriptionFilter, SubscriptionModel, Long>, LogicalDeletedService<Long>{
    SubscriptionModel findByUserAndActivePackage(UUID userId);
    List<SubscriptionModel> findAllExpiredFreeSubscriptions();
}
