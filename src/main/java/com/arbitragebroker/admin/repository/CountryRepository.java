package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.CountryEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends BaseRepository<CountryEntity, Long> {
}
