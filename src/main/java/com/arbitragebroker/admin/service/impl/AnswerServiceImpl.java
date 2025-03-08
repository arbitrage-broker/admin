package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.entity.AnswerEntity;
import com.arbitragebroker.admin.entity.QAnswerEntity;
import com.arbitragebroker.admin.filter.AnswerFilter;
import com.arbitragebroker.admin.mapping.AnswerMapper;
import com.arbitragebroker.admin.model.AnswerModel;
import com.arbitragebroker.admin.repository.AnswerRepository;
import com.arbitragebroker.admin.service.AnswerService;
import com.arbitragebroker.admin.validation.Validation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@Slf4j
public class AnswerServiceImpl extends BaseServiceImpl<AnswerFilter, AnswerModel, AnswerEntity, Long> implements AnswerService {

    private final AnswerRepository repository;
    private final AnswerMapper mapper;
    private final List<Validation<AnswerModel>> validations;
    private final RestTemplate restTemplate;
    @Value("${client.server.url}")
    private String clientServerUrl;

    @Autowired
    public AnswerServiceImpl(AnswerRepository repository, AnswerMapper mapper, List<Validation<AnswerModel>> validations, RestTemplate restTemplate) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
        this.validations = validations;
        this.restTemplate = restTemplate;
    }

    @Override
    public Predicate queryBuilder(AnswerFilter filter) {
        QAnswerEntity p = QAnswerEntity.answerEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getTitle().ifPresent(v->builder.and(p.title.toLowerCase().contains(v.toLowerCase())));
        filter.getQuestionId().ifPresent(v->builder.and(p.question.id.eq(v)));
        filter.getActive().ifPresent(v->builder.and(p.active.eq(v)));

        return builder;
    }

    @Override
    public AnswerModel create(AnswerModel model) {
        validations.forEach(v-> v.validate(model));
        return super.create(model);
    }

    @Override
    @Transactional
    public AnswerModel update(AnswerModel model) {
        validations.forEach(v-> v.validate(model));
        return super.update(model);
    }

    @Override
    public void sendMessage(String msg) {
        log.info("start calling AnswerServiceImpl.sendMessage {}", msg);
        restTemplate.execute(
                clientServerUrl + "/api/v1/answer/receive-message?msg=" + msg,
                HttpMethod.GET,
                null, // No request callback (no request body)
                clientHttpResponse -> {
                    HttpStatusCode statusCode = clientHttpResponse.getStatusCode();
                    if (statusCode.is2xxSuccessful()) { //or other expected status codes
                        log.info("Message successfully sent. Status code: {}", statusCode);
                    } else {
                        log.error("Message failed to send. Status code: {}", statusCode);
                    }
                    clientHttpResponse.getBody().close();

                    return null; // No result to return from the callback
                }
        );
    }
}
