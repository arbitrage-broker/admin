package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.SubscriptionPackageEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface SubscriptionPackageRepository extends BaseRepository<SubscriptionPackageEntity, Long> {
    @Query("SELECT sp FROM SubscriptionPackageEntity sp WHERE :amount BETWEEN sp.price AND sp.maxPrice")
    Optional<SubscriptionPackageEntity> findMatchedPackageByAmount(BigDecimal amount);
    SubscriptionPackageEntity findTopByOrderByMaxPriceDesc();
}
