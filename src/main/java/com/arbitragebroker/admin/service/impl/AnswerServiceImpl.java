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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AnswerServiceImpl extends BaseServiceImpl<AnswerFilter, AnswerModel, AnswerEntity, Long> implements AnswerService {

    private final AnswerRepository repository;
    private final AnswerMapper mapper;
    private final List<Validation<AnswerModel>> validations;

    @Autowired
    public AnswerServiceImpl(AnswerRepository repository, AnswerMapper mapper, List<Validation<AnswerModel>> validations) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
        this.validations = validations;
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
}
