package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.entity.ParameterGroupEntity;
import com.arbitragebroker.admin.filter.ParameterGroupFilter;
import com.arbitragebroker.admin.model.ParameterGroupModel;

public interface ParameterGroupService extends BaseService<ParameterGroupFilter, ParameterGroupModel, Long> , LogicalDeletedService<Long> {
    ParameterGroupEntity findByCode(String code);
}
