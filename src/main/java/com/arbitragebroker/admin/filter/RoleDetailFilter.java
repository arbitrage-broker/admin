package com.arbitragebroker.admin.filter;

import com.arbitragebroker.admin.enums.CurrencyType;
import com.arbitragebroker.admin.enums.NetworkType;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Optional;

@Setter
@ToString
@Accessors(chain = true)
public class RoleDetailFilter {
	private Long id;
	private NetworkType network;
	private CurrencyType currency;
	private Long roleId;
	private String description;

	public Optional<Long> getId() {
		return Optional.ofNullable(id);
	}

	public Optional<NetworkType> getNetwork() {
		return Optional.ofNullable(network);
	}

	public Optional<CurrencyType> getCurrency() {
		return Optional.ofNullable(currency);
	}

	public Optional<Long> getRoleId() {
		return Optional.ofNullable(roleId);
	}

	public Optional<String> getDescription() {
		if (description == null || description.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(description);
	}
}
