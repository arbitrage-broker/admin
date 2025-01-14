package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.controller.LogicalDeletedRestController;
import com.arbitragebroker.admin.filter.SubscriptionFilter;
import com.arbitragebroker.admin.model.SubscriptionModel;
import com.arbitragebroker.admin.service.LogicalDeletedService;
import com.arbitragebroker.admin.service.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Subscription Rest Service v1")
@RequestMapping(value = "/api/v1/subscription")
public  class SubscriptionRestController extends BaseRestControllerImpl<SubscriptionFilter, SubscriptionModel, Long> implements LogicalDeletedRestController<Long> {

    private SubscriptionService subscriptionService;

    public SubscriptionRestController(SubscriptionService service) {
        super(service);
        this.subscriptionService = service;
    }


    @Override
    public LogicalDeletedService<Long> getService() {
        return subscriptionService;
    }
}
