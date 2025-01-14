package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.filter.ExchangeFilter;
import com.arbitragebroker.admin.model.ExchangeModel;
import com.arbitragebroker.admin.service.ExchangeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Exchange Rest Service v1")
@RequestMapping(value = "/api/v1/exchange")
public class ExchangeRestController extends BaseRestControllerImpl<ExchangeFilter, ExchangeModel, Long>  {

    private ExchangeService exchangeService;

    public ExchangeRestController(ExchangeService service) {
        super(service);
        this.exchangeService = service;
    }
}
