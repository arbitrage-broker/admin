package com.arbitragebroker.admin.mapping;

import com.arbitragebroker.admin.entity.CoinEntity;
import com.arbitragebroker.admin.model.CoinModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CoinMapper extends BaseMapper<CoinModel, CoinEntity> {
}
