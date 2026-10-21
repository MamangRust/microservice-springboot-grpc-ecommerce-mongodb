package com.sanedge.ecommerce.transaction.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionYearlyAmountSuccess {
    private String year;
    private Long totalSuccess;
    private Long totalAmount;
}
