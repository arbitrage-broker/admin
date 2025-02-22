package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.SubscriptionPackageDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPackageDetailRepository extends BaseRepository<SubscriptionPackageDetailEntity, Long> {
    Page<SubscriptionPackageDetailEntity> findBySubscriptionPackageId(long subscriptionPackageId, Pageable pageable);
}
