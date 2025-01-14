package com.arbitragebroker.admin.model;

import lombok.Data;

@Data
public class ParameterGroupModel extends BaseModel<Long> {
    private String code;
    private String title;
}
