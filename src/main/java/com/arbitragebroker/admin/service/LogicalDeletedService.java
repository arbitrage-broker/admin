package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import com.arbitragebroker.admin.entity.LogicalDeleted;

import java.io.Serializable;

public interface LogicalDeletedService<ID extends Serializable> {
    <E extends LogicalDeleted> JpaRepository<E, ID> getRepository();

    default void logicalDeleteById(ID id) {
        var entity = getRepository().findById(id).orElseThrow(() -> new NotFoundException("id: " + id));
        entity.setDeleted(true);
        getRepository().save(entity);
    }
}