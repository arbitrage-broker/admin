package com.arbitragebroker.admin.mapping;


import com.arbitragebroker.admin.entity.NotificationEntity;
import com.arbitragebroker.admin.model.NotificationModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {ParameterGroupMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface NotificationMapper extends BaseMapper<NotificationModel, NotificationEntity> {

}
