package com.arbitragebroker.admin.mapping;

import com.arbitragebroker.admin.entity.RoleDetailEntity;
import com.arbitragebroker.admin.model.RoleDetailModel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoleDetailMapper extends BaseMapper<RoleDetailModel, RoleDetailEntity> {
}
