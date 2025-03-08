package com.arbitragebroker.admin.entity;

import com.arbitragebroker.admin.enums.CurrencyType;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;

import static com.arbitragebroker.admin.util.MapperHelper.getOrDefault;

@Data
@Entity
@Table(name = "tbl_arbitrage")
public class ArbitrageEntity extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="seq_arbitrage", sequenceName="seq_arbitrage",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_arbitrage")
	private Long id;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
	@ManyToOne
	@JoinColumn(name = "exchange_id")
	private ExchangeEntity exchange;
	@ManyToOne
	@JoinColumn(name = "coin_id")
	private CoinEntity coin;
	@ManyToOne
	@JoinColumn(name = "subscription_id")
	private SubscriptionEntity subscription;

	private BigDecimal reward;
	@Enumerated(value = EnumType.STRING)
	private CurrencyType currency;
	private String role;

	@Override
	public String getSelectTitle() {
		return getOrDefault(()->user.getSelectTitle(),"").concat(" ").concat(getOrDefault(()->coin.getName(),"")).concat(" ").concat(getOrDefault(()->exchange.getName(),""));
	}
}
