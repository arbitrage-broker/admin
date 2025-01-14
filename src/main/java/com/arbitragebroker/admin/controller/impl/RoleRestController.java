package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.filter.RoleFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.arbitragebroker.admin.model.RoleModel;
import com.arbitragebroker.admin.service.RoleService;


@RestController
@Tag(name = "Role Rest Service v1")
@RequestMapping(value = "/api/v1/role")
public class RoleRestController extends BaseRestControllerImpl<RoleFilter, RoleModel, Long> {

    private RoleService roleService;

    public RoleRestController(RoleService service) {
        super(service);
        this.roleService = service;
    }
}
