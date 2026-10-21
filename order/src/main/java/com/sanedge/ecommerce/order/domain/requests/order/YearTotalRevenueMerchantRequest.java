package com.sanedge.ecommerce.order.domain.requests.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class YearTotalRevenueMerchantRequest {

    @NotNull(message = "ID merchant wajib diisi")
    private Integer merchantId;

    @NotNull(message = "Tahun wajib diisi")
    @Min(value = 1900, message = "Tahun harus valid")
    private Integer year;
}
