package com.sanedge.ecommerce.order.domain.requests.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MonthOrderMerchantRequest {

    @NotNull(message = "ID merchant wajib diisi")
    private Integer merchantId;

    @NotNull(message = "Tahun wajib diisi")
    private Integer year;
}
