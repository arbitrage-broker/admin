package com.arbitragebroker.admin.model;

import lombok.Data;

@Data
public class ExchangeModel extends BaseModel<Long> {
    private String name;
    private String logo;
}
