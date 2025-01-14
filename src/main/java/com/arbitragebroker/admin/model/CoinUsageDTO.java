package com.arbitragebroker.admin.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoinUsageDTO {
    private String name;
    private Long usageCount;
    private Double usagePercentage;
}
