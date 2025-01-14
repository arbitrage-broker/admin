package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.CoinEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinRepository extends BaseRepository<CoinEntity, Long> {
}
