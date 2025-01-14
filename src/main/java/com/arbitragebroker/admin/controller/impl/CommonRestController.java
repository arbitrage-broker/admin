package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.strategy.NetworkStrategy;
import com.arbitragebroker.admin.strategy.NetworkStrategyFactory;
import com.arbitragebroker.admin.enums.NetworkType;
import com.arbitragebroker.admin.model.Select2Model;
import com.arbitragebroker.admin.util.ReflectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Common Rest Service v1")
@RequestMapping(value = "/api/v1/common")
@RequiredArgsConstructor
public class CommonRestController {
    private final NetworkStrategyFactory networkStrategyFactory;
    @GetMapping("/getEnum/{name}")
    public ResponseEntity<List<Select2Model>> getEnum(@PathVariable String name) {
        List<Select2Model> list = (List<Select2Model>) ReflectionUtil.invokeMethod("com.arbitragebroker.admin.enums.".concat(name), "getAll");
        return ResponseEntity.ok(list);
    }
    @GetMapping("/price/{network}/{currency}")
    public ResponseEntity<BigDecimal> getPrice(@PathVariable NetworkType network, @PathVariable String currency) {
        NetworkStrategy networkStrategy = networkStrategyFactory.get(network);
        return ResponseEntity.ok(networkStrategy.getPrice(currency));
    }
    @GetMapping(value = {"/get-html" +
            "-file/{name}"})
    public ModelAndView getHtml(@PathVariable String name, @RequestParam("params") String json) throws IOException {
        Map<String, String> map = new ObjectMapper().readValue(json, Map.class);
        ModelAndView modelAndView = new ModelAndView();
        map.forEach((k, v) -> {
            modelAndView.addObject(k, v);
        });
        modelAndView.setViewName(name);
        return modelAndView;
    }
}
