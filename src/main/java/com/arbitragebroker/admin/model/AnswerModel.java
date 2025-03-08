package com.arbitragebroker.admin.model;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class AnswerModel extends BaseModel<Long> {
    @NotNull
    private QuestionModel question;
    @NotNull
    private String title;
    private Integer displayOrder;
    @NotNull
    private boolean active;

}
