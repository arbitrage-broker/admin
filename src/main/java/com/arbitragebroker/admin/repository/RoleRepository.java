package com.arbitragebroker.admin.repository;

import org.springframework.stereotype.Repository;
import com.arbitragebroker.admin.entity.RoleEntity;

import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRole(String role);
}
