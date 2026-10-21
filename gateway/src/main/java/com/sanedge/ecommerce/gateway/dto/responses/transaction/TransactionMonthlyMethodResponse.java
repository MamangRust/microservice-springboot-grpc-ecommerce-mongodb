package com.sanedge.ecommerce.gateway.dto.responses.transaction;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMonthlyMethodResponse {
    private String month;
    private String paymentMethod;
    private Long totalTransactions;
    private Long totalAmount;


}
