package com.arbitragebroker.admin.model;

import lombok.Data;

@Data
public class ParameterModel extends BaseModel<Long> {
    private String code;
    private String title;
    private String value;
    private ParameterGroupModel parameterGroup;
}
