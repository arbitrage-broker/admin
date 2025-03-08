package com.arbitragebroker.admin.model;

import com.arbitragebroker.admin.enums.CurrencyType;
import com.arbitragebroker.admin.enums.EntityStatusType;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SubscriptionPackageModel extends BaseModel<Long> {
    @NotEmpty
    private String name;
    private Integer duration;//days
    @NotNull
    private Integer orderCount;
    @NotNull
    private BigDecimal price;
    private BigDecimal maxPrice;
    @NotNull
    private CurrencyType currency;
    private String description;
    private EntityStatusType status;
    private Float minTradingReward;
    private Float maxTradingReward;
    private Float parentReferralBonus;
    private int withdrawalDurationPerDay;
    private int userProfitPercentage;
    private int siteProfitPercentage;
    public SubscriptionPackageModel setSubscriptionPackageId(Long id){
        setId(id);
        return this;
    }
}
