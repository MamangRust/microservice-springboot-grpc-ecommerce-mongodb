package com.sanedge.ecommerce.common.adapter.merchant;

import com.sanedge.ecommerce.common.adapter.model.Merchant;

/**
 * Port baca merchant. Setara {@code merchant.QueryRepository} di Go: konsumen
 * bergantung pada kontrak ini, bukan pada stub gRPC.
 */
public interface MerchantPort {

    /** @throws com.sanedge.ecommerce.common.exception.ResourceNotFoundException bila merchant tidak ada. */
    Merchant findById(int merchantId);
}
