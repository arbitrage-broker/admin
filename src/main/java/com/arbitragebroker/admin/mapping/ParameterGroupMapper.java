package com.arbitragebroker.admin.mapping;

import com.arbitragebroker.admin.entity.ParameterGroupEntity;
import com.arbitragebroker.admin.model.ParameterGroupModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ParameterGroupMapper extends BaseMapper<ParameterGroupModel, ParameterGroupEntity> {
}
