package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.filter.AnswerFilter;
import com.arbitragebroker.admin.model.AnswerModel;
import com.arbitragebroker.admin.service.AnswerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Answer Rest Service v1")
@RequestMapping(value = "/api/v1/answer")
public class AnswerRestController extends BaseRestControllerImpl<AnswerFilter, AnswerModel, Long>  {

    private AnswerService answerService;

    public AnswerRestController(AnswerService service) {
        super(service);
        this.answerService = service;
    }
    @GetMapping("send-message")
    public ResponseEntity<Void> sendMessage(@RequestParam String msg){
        answerService.sendMessage(msg);
        return ResponseEntity.noContent().build();
    }
}
