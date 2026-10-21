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
public class TransactionMonthlyAmountFailedResponse {
    private String year;
    private String month;
    private Long totalFailed;
    private Long totalAmount;


}