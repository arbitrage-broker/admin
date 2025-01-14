package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.filter.CoinFilter;
import com.arbitragebroker.admin.model.CoinModel;
import com.arbitragebroker.admin.service.CoinService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Coin Rest Service v1")
@RequestMapping(value = "/api/v1/coin")
public class CoinRestController extends BaseRestControllerImpl<CoinFilter, CoinModel, Long>  {

    private CoinService coinService;

    public CoinRestController(CoinService service) {
        super(service);
        this.coinService = service;
    }
}
