package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.filter.RoleFilter;
import com.arbitragebroker.admin.model.RoleModel;

public interface RoleService extends BaseService<RoleFilter,RoleModel, Long> {
    RoleModel findByRole(String role);
}
