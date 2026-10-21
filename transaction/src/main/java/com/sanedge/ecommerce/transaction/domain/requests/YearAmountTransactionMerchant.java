package com.sanedge.ecommerce.transaction.domain.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class YearAmountTransactionMerchant {

    @NotNull
    private Integer merchantId;

    @NotNull
    private Integer year;
}
