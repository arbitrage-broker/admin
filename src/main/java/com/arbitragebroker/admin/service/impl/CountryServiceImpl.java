package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.entity.CountryEntity;
import com.arbitragebroker.admin.entity.QCountryEntity;
import com.arbitragebroker.admin.filter.CountryFilter;
import com.arbitragebroker.admin.mapping.CountryMapper;
import com.arbitragebroker.admin.model.CountryModel;
import com.arbitragebroker.admin.repository.CountryRepository;
import com.arbitragebroker.admin.service.CountryService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CountryServiceImpl extends BaseServiceImpl<CountryFilter, CountryModel, CountryEntity, Long> implements CountryService {

    private CountryRepository repository;
    private CountryMapper mapper;

    @Autowired
    public CountryServiceImpl(CountryRepository repository, CountryMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Predicate queryBuilder(CountryFilter filter) {
        QCountryEntity p = QCountryEntity.countryEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getName().ifPresent(v->builder.and(p.name.toLowerCase().contains(v.toLowerCase())));

        return builder;
    }
}
