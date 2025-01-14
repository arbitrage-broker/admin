package com.arbitragebroker.admin.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class SubscriptionPackageDetailModel extends BaseModel<Long> {
    @NotNull
    private SubscriptionPackageModel subscriptionPackage;
    private BigDecimal amount;
    private BigDecimal minProfit;
    private BigDecimal maxProfit;
}
