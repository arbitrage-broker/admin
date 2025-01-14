package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.filter.RoleDetailFilter;
import com.arbitragebroker.admin.model.RoleDetailModel;
import com.arbitragebroker.admin.service.RoleDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "Role Rest Service v1")
@RequestMapping(value = "/api/v1/role-detail")
public class RoleDetailRestController extends BaseRestControllerImpl<RoleDetailFilter, RoleDetailModel, Long> {

    private RoleDetailService roleDetailService;

    public RoleDetailRestController(RoleDetailService service) {
        super(service);
        this.roleDetailService = service;
    }
}
