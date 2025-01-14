package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends BaseRepository<AnswerEntity, Long> {
    long countAllByIdIsNotAndQuestionId(Long id,Long questionId);
    List<AnswerEntity> findAllByQuestionId(Long questionId);
}
