package com.arbitragebroker.admin.mapping;

import com.arbitragebroker.admin.entity.AnswerEntity;
import com.arbitragebroker.admin.entity.QuestionEntity;
import com.arbitragebroker.admin.model.AnswerModel;
import com.arbitragebroker.admin.model.QuestionModel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface QuestionMapper extends BaseMapper<QuestionModel, QuestionEntity> {
    @Override
    @Mappings({
            @Mapping(target = "answers", qualifiedByName = "answerEntityToAnswerModel")
    })
    QuestionModel toModel(final QuestionEntity entity);

    @Named("answerEntityToAnswerModel")
    @Mapping(target = "question", ignore = true)
    AnswerModel answerEntityToAnswerModel(AnswerEntity entity);
}
