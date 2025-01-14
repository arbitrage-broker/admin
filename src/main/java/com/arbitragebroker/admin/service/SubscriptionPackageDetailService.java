package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.filter.SubscriptionPackageDetailFilter;
import com.arbitragebroker.admin.model.SubscriptionPackageDetailModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubscriptionPackageDetailService extends BaseService<SubscriptionPackageDetailFilter, SubscriptionPackageDetailModel, Long>{
    Page<SubscriptionPackageDetailModel> findBySubscriptionPackageId(long subscriptionPackageId, Pageable pageable);
}
