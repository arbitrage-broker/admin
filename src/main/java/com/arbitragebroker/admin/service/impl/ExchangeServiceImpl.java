package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.entity.ExchangeEntity;
import com.arbitragebroker.admin.entity.QExchangeEntity;
import com.arbitragebroker.admin.filter.ExchangeFilter;
import com.arbitragebroker.admin.mapping.ExchangeMapper;
import com.arbitragebroker.admin.model.ExchangeModel;
import com.arbitragebroker.admin.repository.ExchangeRepository;
import com.arbitragebroker.admin.service.ExchangeService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ExchangeServiceImpl extends BaseServiceImpl<ExchangeFilter, ExchangeModel, ExchangeEntity, Long> implements ExchangeService {

    private ExchangeRepository repository;
    private ExchangeMapper mapper;

    @Autowired
    public ExchangeServiceImpl(ExchangeRepository repository, ExchangeMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Predicate queryBuilder(ExchangeFilter filter) {
        QExchangeEntity p = QExchangeEntity.exchangeEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getName().ifPresent(v->builder.and(p.name.toLowerCase().contains(v.toLowerCase())));
        filter.getLogo().ifPresent(v->builder.and(p.logo.eq(v)));

        return builder;
    }
}
