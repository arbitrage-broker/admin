package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.filter.ArbitrageFilter;
import com.arbitragebroker.admin.model.ArbitrageModel;
import com.arbitragebroker.admin.service.ArbitrageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Arbitrage Rest Service v1")
@RequestMapping(value = "/api/v1/arbitrage")
public class ArbitrageRestController extends BaseRestControllerImpl<ArbitrageFilter, ArbitrageModel, Long>  {

    private ArbitrageService arbitrageService;

    public ArbitrageRestController(ArbitrageService service) {
        super(service);
        this.arbitrageService = service;
    }
    @GetMapping("count-all-by-user/{userId}")
    public long countAllByUserId(@PathVariable UUID userId) {
        return arbitrageService.countAllByUserId(userId);
    }
}
