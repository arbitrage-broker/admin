package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.RoleDetailEntity;
import com.arbitragebroker.admin.enums.CurrencyType;
import com.arbitragebroker.admin.enums.NetworkType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleDetailRepository extends BaseRepository<RoleDetailEntity, Long>{
    Optional<RoleDetailEntity> findByRoleRoleAndNetworkAndCurrency(String role, NetworkType networkType, CurrencyType currencyType);
}
