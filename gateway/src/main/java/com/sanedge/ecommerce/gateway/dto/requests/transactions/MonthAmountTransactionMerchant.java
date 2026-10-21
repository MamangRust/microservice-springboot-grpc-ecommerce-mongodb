package com.sanedge.ecommerce.gateway.dto.requests.transactions;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request untuk mengambil jumlah transaksi bulanan berdasarkan merchant")
public class MonthAmountTransactionMerchant {

    @NotNull
    @Schema(description = "ID merchant", example = "123", required = true)
    private Integer merchantId;

    @NotNull
    @Schema(description = "Tahun transaksi", example = "2025", required = true)
    private Integer year;

    @NotNull
    @Schema(description = "Bulan transaksi", example = "9", required = true)
    private Integer month;
}
