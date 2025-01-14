package com.arbitragebroker.admin.mapping;


import com.arbitragebroker.admin.entity.SubscriptionPackageDetailEntity;
import com.arbitragebroker.admin.model.SubscriptionPackageDetailModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {SubscriptionPackageMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface SubscriptionPackageDetailMapper extends BaseMapper<SubscriptionPackageDetailModel, SubscriptionPackageDetailEntity> {

}
