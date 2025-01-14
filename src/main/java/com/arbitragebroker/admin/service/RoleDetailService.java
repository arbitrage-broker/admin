package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.enums.CurrencyType;
import com.arbitragebroker.admin.enums.NetworkType;
import com.arbitragebroker.admin.filter.RoleDetailFilter;
import com.arbitragebroker.admin.model.RoleDetailModel;

import javax.validation.constraints.NotNull;

public interface RoleDetailService extends BaseService<RoleDetailFilter,RoleDetailModel, Long> {
    RoleDetailModel findByRoleNameAndNetworkAndCurrency(String role, NetworkType network, @NotNull CurrencyType currency);
}
