package com.arbitragebroker.admin.mapping;


import com.arbitragebroker.admin.entity.ParameterEntity;
import com.arbitragebroker.admin.model.ParameterModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {ParameterGroupMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ParameterMapper extends BaseMapper<ParameterModel, ParameterEntity> {

}
