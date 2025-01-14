package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.controller.LogicalDeletedRestController;
import com.arbitragebroker.admin.filter.ParameterGroupFilter;
import com.arbitragebroker.admin.model.ParameterGroupModel;
import com.arbitragebroker.admin.service.LogicalDeletedService;
import com.arbitragebroker.admin.service.ParameterGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "ParameterGroup Rest Service v1")
@RequestMapping(value = "/api/v1/parameterGroup")
public class ParameterGroupRestController extends BaseRestControllerImpl<ParameterGroupFilter, ParameterGroupModel, Long> implements LogicalDeletedRestController<Long> {

    private ParameterGroupService parameterGroupService;

    public ParameterGroupRestController(ParameterGroupService service) {
        super(service);
        this.parameterGroupService = service;
    }
    @Override
    public LogicalDeletedService<Long> getService() {
        return parameterGroupService;
    }
}
