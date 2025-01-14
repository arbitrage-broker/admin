package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.entity.CoinEntity;
import com.arbitragebroker.admin.entity.QCoinEntity;
import com.arbitragebroker.admin.filter.CoinFilter;
import com.arbitragebroker.admin.mapping.CoinMapper;
import com.arbitragebroker.admin.model.CoinModel;
import com.arbitragebroker.admin.repository.CoinRepository;
import com.arbitragebroker.admin.service.CoinService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CoinServiceImpl extends BaseServiceImpl<CoinFilter, CoinModel, CoinEntity, Long> implements CoinService {

    private CoinRepository repository;
    private CoinMapper mapper;

    @Autowired
    public CoinServiceImpl(CoinRepository repository, CoinMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Predicate queryBuilder(CoinFilter filter) {
        QCoinEntity p = QCoinEntity.coinEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getName().ifPresent(v->builder.and(p.name.toLowerCase().contains(v.toLowerCase())));
        filter.getLogo().ifPresent(v->builder.and(p.logo.eq(v)));

        return builder;
    }
}
