package com.arbitragebroker.admin.model;

import com.arbitragebroker.admin.enums.AnswerType;
import com.arbitragebroker.admin.enums.QuestionType;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class QuestionModel extends BaseModel<Long> {
    @NotNull
    private String title;
    private Integer displayOrder;
    @NotNull
    private QuestionType type;
    @NotNull
    private AnswerType answerType;
    private UserModel user;
    private List<AnswerModel> answers = new ArrayList<>();;
    private boolean active;
}
