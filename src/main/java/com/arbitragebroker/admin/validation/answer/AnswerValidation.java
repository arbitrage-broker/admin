package com.arbitragebroker.admin.validation.answer;

import com.arbitragebroker.admin.enums.AnswerType;
import com.arbitragebroker.admin.model.AnswerModel;
import com.arbitragebroker.admin.repository.AnswerRepository;
import com.arbitragebroker.admin.repository.QuestionRepository;
import com.arbitragebroker.admin.validation.Validation;
import com.arbitragebroker.admin.exception.BadRequestException;
import com.arbitragebroker.admin.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.arbitragebroker.admin.util.MapperHelper.get;

@Component
@RequiredArgsConstructor
public class AnswerValidation implements Validation<AnswerModel> {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    @Override
    public void validate(AnswerModel answerModel, Object... args) {
        AnswerType answerType = get(()->answerModel.getQuestion().getAnswerType());
        if(answerType == null) {
            var question = questionRepository.findById(answerModel.getQuestion().getId()).orElseThrow(() -> new NotFoundException("Question not found by id " + answerModel.getQuestion().getId()));
            answerType = question.getAnswerType();
        }

        if(!answerType.equals(AnswerType.MULTIPLE)) {
            Long answerId = 0L;
            if(answerModel.getId() != null)
                answerId = answerModel.getId();
            if(answerRepository.countAllByIdIsNotAndQuestionId(answerId, answerModel.getQuestion().getId()) > 0)
                throw new BadRequestException("This question can not have multiple answer");
        }
        if(answerType.equals(AnswerType.INTEGER) && !isInteger(answerModel.getTitle())){
            throw new BadRequestException("This question should have single Integer answer, please change the answer to integer format.");
        }
        if(answerType.equals(AnswerType.BOOLEAN) && !isBoolean(answerModel.getTitle())){
            throw new BadRequestException("This question should have single Integer answer, please change the answer to boolean format.");
        }

    }
    private boolean isInteger(String value){
        try {
            Integer.valueOf(value);
            return true;
        } catch (Exception e){
            return false;
        }
    }
    private boolean isBoolean(String value){
        if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("no"))
            return true;
        return false;
    }
}
