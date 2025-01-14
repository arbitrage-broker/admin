package com.arbitragebroker.admin.mapping;

import com.arbitragebroker.admin.entity.SubscriptionPackageEntity;
import com.arbitragebroker.admin.model.SubscriptionPackageModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface SubscriptionPackageMapper extends BaseMapper<SubscriptionPackageModel, SubscriptionPackageEntity> {

}
