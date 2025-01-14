package com.arbitragebroker.admin.model;

import com.arbitragebroker.admin.enums.CurrencyType;
import com.arbitragebroker.admin.enums.EntityStatusType;
import com.arbitragebroker.admin.enums.NetworkType;
import com.arbitragebroker.admin.enums.TransactionType;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class WalletModel extends BaseModel<Long> {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal actualAmount;
    @NotNull
    private TransactionType transactionType;
    @NotNull
    private String transactionHash;
    @NotNull
    private CurrencyType currency = CurrencyType.USDT;
    private NetworkType network = NetworkType.TRC20;
    private UserModel user;
    private String address;
    private EntityStatusType status = EntityStatusType.Pending;
    private String role;
    private Long roleId;

}
