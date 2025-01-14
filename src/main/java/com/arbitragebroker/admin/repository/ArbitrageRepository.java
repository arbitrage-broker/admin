package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.ArbitrageEntity;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArbitrageRepository extends BaseRepository<ArbitrageEntity, Long> {
    long countAllByUserId(UUID userId);
}
