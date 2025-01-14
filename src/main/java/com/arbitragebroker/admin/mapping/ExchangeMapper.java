package com.arbitragebroker.admin.mapping;

import com.arbitragebroker.admin.entity.ExchangeEntity;
import com.arbitragebroker.admin.model.ExchangeModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExchangeMapper extends BaseMapper<ExchangeModel, ExchangeEntity> {
}
