package com.arbitragebroker.admin.service.impl;

import com.arbitragebroker.admin.entity.QRoleEntity;
import com.arbitragebroker.admin.entity.RoleEntity;
import com.arbitragebroker.admin.enums.RoleType;
import com.arbitragebroker.admin.filter.RoleFilter;
import com.arbitragebroker.admin.mapping.RoleMapper;
import com.arbitragebroker.admin.model.RoleModel;
import com.arbitragebroker.admin.repository.RoleRepository;
import com.arbitragebroker.admin.service.RoleService;
import com.arbitragebroker.admin.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends BaseServiceImpl<RoleFilter,RoleModel, RoleEntity, Long> implements RoleService {

    private final RoleRepository repository;

    public RoleServiceImpl(RoleRepository repository, RoleMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
    }

    @Override
    public Predicate queryBuilder(RoleFilter filter) {
        QRoleEntity path = QRoleEntity.roleEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if(!RoleType.hasRole(RoleType.ADMIN)) {
            builder.and(path.role.ne(RoleType.ADMIN));
        }

        filter.getId().ifPresent(value -> builder.and(path.id.eq(value)));
        filter.getRole().ifPresent(value -> builder.and(path.role.eq(value)));
        filter.getTitle().ifPresent(value -> builder.and(path.title.eq(value)));

        return builder;
    }

    @Override
    public RoleModel findByRole(String role) {
        return mapper.toModel(repository.findByRole(role).orElseThrow(() -> new NotFoundException("role: " + role)));
    }
}