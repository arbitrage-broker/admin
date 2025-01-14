package com.arbitragebroker.admin.model;

import com.arbitragebroker.admin.enums.CurrencyType;
import com.arbitragebroker.admin.enums.NetworkType;
import lombok.Data;

@Data
public class RoleDetailModel extends BaseModel<Long> {
	private RoleModel role;
	private NetworkType network;
	private CurrencyType currency;
	private String address;
	private String description;
}
