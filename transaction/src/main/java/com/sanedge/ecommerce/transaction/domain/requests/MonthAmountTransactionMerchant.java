package com.sanedge.ecommerce.transaction.domain.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MonthAmountTransactionMerchant {

    @NotNull
    private Integer merchantId;

    @NotNull
    private Integer year;

    @NotNull
    private Integer month;
}
