package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.filter.ParameterFilter;
import com.arbitragebroker.admin.model.ParameterModel;

import java.util.List;

public interface ParameterService extends BaseService<ParameterFilter, ParameterModel, Long> , LogicalDeletedService<Long>{
    ParameterModel findByCode(String code);
    List<ParameterModel> findAllByParameterGroupCode(String parameterGroupCode);
}
