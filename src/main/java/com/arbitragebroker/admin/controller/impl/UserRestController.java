package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.filter.UserFilter;
import com.arbitragebroker.admin.model.UserModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.arbitragebroker.admin.service.UserService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@Tag(name = "User Rest Service v1")
@RequestMapping(value = "/api/v1/user")
public class UserRestController extends BaseRestControllerImpl<UserFilter, UserModel, UUID> {

    private UserService userService;

    public UserRestController(UserService service) {
        super(service);
        this.userService = service;
    }

    @PostMapping("/register")
    public ResponseEntity<UserModel> register(@Valid @RequestBody UserModel model) {
        return ResponseEntity.ok(userService.register(model));
    }
}
