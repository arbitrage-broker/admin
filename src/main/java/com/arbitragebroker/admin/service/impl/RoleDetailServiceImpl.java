package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.entity.RoleDetailEntity;
import com.arbitragebroker.admin.entity.QRoleDetailEntity;
import com.arbitragebroker.admin.enums.CurrencyType;
import com.arbitragebroker.admin.enums.NetworkType;
import com.arbitragebroker.admin.filter.RoleDetailFilter;
import com.arbitragebroker.admin.mapping.RoleDetailMapper;
import com.arbitragebroker.admin.model.RoleDetailModel;
import com.arbitragebroker.admin.repository.RoleDetailRepository;
import com.arbitragebroker.admin.service.RoleDetailService;
import com.arbitragebroker.admin.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RoleDetailServiceImpl extends BaseServiceImpl<RoleDetailFilter, RoleDetailModel, RoleDetailEntity, Long> implements RoleDetailService {

    private RoleDetailRepository roleDetailRepository;
    private RoleDetailMapper mapper;

    @Autowired
    public RoleDetailServiceImpl(RoleDetailRepository roleDetailRepository, RoleDetailMapper mapper) {
        super(roleDetailRepository, mapper);
        this.roleDetailRepository = roleDetailRepository;
        this.mapper = mapper;
    }

    @Override
    public Predicate queryBuilder(RoleDetailFilter filter) {
        QRoleDetailEntity p = QRoleDetailEntity.roleDetailEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getNetwork().ifPresent(v->builder.and(p.network.eq(v)));
        filter.getCurrency().ifPresent(v->builder.and(p.currency.eq(v)));
        filter.getRoleId().ifPresent(v->builder.and(p.role.id.eq(v)));
        filter.getDescription().ifPresent(v->builder.and(p.description.contains(v)));

        return builder;
    }

    @Override
    public RoleDetailModel findByRoleNameAndNetworkAndCurrency(String role, NetworkType network, CurrencyType currency) {
        var entity = roleDetailRepository.findByRoleRoleAndNetworkAndCurrency(role,network,currency).orElseThrow(()->new NotFoundException("RoleDetail not found"));
        return mapper.toModel(entity);
    }
}
