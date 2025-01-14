package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.ParameterEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParameterRepository extends BaseRepository<ParameterEntity, Long> {
	Optional<ParameterEntity> findByCode(String code);
}
