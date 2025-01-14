package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.ExchangeEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRepository extends BaseRepository<ExchangeEntity, Long> {
}
