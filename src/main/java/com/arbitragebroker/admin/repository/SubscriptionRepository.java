package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.SubscriptionEntity;
import com.arbitragebroker.admin.enums.EntityStatusType;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends BaseRepository<SubscriptionEntity, Long> {
    SubscriptionEntity findByUserIdAndStatus(UUID userId, EntityStatusType status);

    List<SubscriptionEntity> findAllBySubscriptionPackageIdAndStatusAndExpireDateBefore(Long subscriptionPackageId, EntityStatusType status, LocalDateTime dateTime);
}
