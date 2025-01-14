package com.arbitragebroker.admin.service;

import com.arbitragebroker.admin.filter.ArbitrageFilter;
import com.arbitragebroker.admin.model.ArbitrageModel;

import java.util.UUID;

public interface ArbitrageService extends BaseService<ArbitrageFilter, ArbitrageModel, Long> {
    long countAllByUserId(UUID userId);
}
