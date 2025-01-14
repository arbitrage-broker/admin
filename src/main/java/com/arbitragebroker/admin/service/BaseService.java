package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.model.Select2Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.arbitragebroker.admin.model.BaseModel;
import com.arbitragebroker.admin.model.PageModel;
import org.springframework.data.history.Revision;

import java.io.Serializable;

public interface BaseService<F, M extends BaseModel<ID>, ID extends Serializable> {
    Page<M> findAll(F filter, Pageable pageable);
    Page<Revision<Long, M>> findAllHistory(ID id, Pageable pageable);
    PageModel<M> findAllTable(F filter, Pageable pageable);
    Page<Select2Model> findAllSelect(F filter, Pageable pageable);
    Long countAll(F filter);
    boolean exists(F filter);
    M findById(ID id);
    Revision<Long, M> findHistoryAtRevision(ID id, Long revisionId);
    M create(M model);
    M update(M model);
    void deleteById(ID id);
}