package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.controller.LogicalDeletedRestController;
import com.arbitragebroker.admin.filter.ParameterFilter;
import com.arbitragebroker.admin.model.ParameterModel;
import com.arbitragebroker.admin.service.LogicalDeletedService;
import com.arbitragebroker.admin.service.ParameterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Parameter Rest Service v1")
@RequestMapping(value = "/api/v1/parameter")
public class ParameterRestController extends BaseRestControllerImpl<ParameterFilter, ParameterModel, Long> implements LogicalDeletedRestController<Long> {

    private ParameterService parameterService;

    public ParameterRestController(ParameterService service) {
        super(service);
        this.parameterService = service;
    }
    @Override
    public LogicalDeletedService<Long> getService() {
        return parameterService;
    }
}
