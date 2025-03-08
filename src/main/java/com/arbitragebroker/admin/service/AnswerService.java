package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.filter.AnswerFilter;
import com.arbitragebroker.admin.model.AnswerModel;

public interface AnswerService extends BaseService<AnswerFilter, AnswerModel, Long> {
    void sendMessage(String msg);
}
