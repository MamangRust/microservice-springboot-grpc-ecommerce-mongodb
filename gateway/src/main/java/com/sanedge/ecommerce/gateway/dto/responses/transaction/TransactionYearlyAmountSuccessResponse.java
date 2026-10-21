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
public class TransactionYearlyAmountSuccessResponse {
    private String year;
    private Long totalSuccess;
    private Long totalAmount;


}