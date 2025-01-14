package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.ParameterGroupEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterGroupRepository extends BaseRepository<ParameterGroupEntity, Long> {
	ParameterGroupEntity findByCode(String role);
}
