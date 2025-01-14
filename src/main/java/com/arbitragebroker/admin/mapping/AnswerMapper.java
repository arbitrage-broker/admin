package com.arbitragebroker.admin.mapping;

import com.arbitragebroker.admin.entity.AnswerEntity;
import com.arbitragebroker.admin.entity.QuestionEntity;
import com.arbitragebroker.admin.model.AnswerModel;
import com.arbitragebroker.admin.model.QuestionModel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AnswerMapper extends BaseMapper<AnswerModel, AnswerEntity> {
    @Override
    @Mappings({
            @Mapping(target = "question", qualifiedByName = "questionEntityToQuestionModel")
    })
    AnswerModel toModel(final AnswerEntity entity);

    @Named("questionEntityToQuestionModel")
    @Mapping(target = "answers", ignore = true)
    QuestionModel questionEntityToQuestionModel(QuestionEntity entity);
}
