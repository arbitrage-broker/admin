package com.arbitragebroker.admin.mapping;

import com.arbitragebroker.admin.entity.ArbitrageEntity;
import com.arbitragebroker.admin.model.ArbitrageModel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ArbitrageMapper extends BaseMapper<ArbitrageModel, ArbitrageEntity> {

    @Override
    @Mappings({
            @Mapping(target = "user.parent", ignore = true),
            @Mapping(target = "user.roles", ignore = true)
    })
    ArbitrageModel toModel(final ArbitrageEntity entity);

}
