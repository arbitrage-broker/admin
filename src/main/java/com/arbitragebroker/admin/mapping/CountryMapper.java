package com.arbitragebroker.admin.mapping;

import com.arbitragebroker.admin.entity.CountryEntity;
import com.arbitragebroker.admin.model.CountryModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CountryMapper extends BaseMapper<CountryModel, CountryEntity> {
}
