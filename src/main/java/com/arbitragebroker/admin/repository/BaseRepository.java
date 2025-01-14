package com.arbitragebroker.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import com.arbitragebroker.admin.entity.BaseEntity;
import org.springframework.data.repository.history.RevisionRepository;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity<ID>,ID extends Serializable> extends RevisionRepository<T, ID, Long>, JpaRepository<T, ID>,QuerydslPredicateExecutor<T> {
}
