package com.sanedge.ecommerce.common.adapter.product;

import java.util.List;

import com.sanedge.ecommerce.common.adapter.model.Product;
import com.sanedge.ecommerce.common.adapter.support.Paged;

/**
 * Port product: baca + tulis (stok) + enumerasi. Setara
 * {@code product.QueryRepository}/{@code CommandRepository}/{@code BulkRepository}
 * di Go.
 */
public interface ProductPort {

    /** @throws com.sanedge.ecommerce.common.exception.ResourceNotFoundException bila tidak ada. */
    Product findById(int productId);

    List<Integer> findIdsByMerchant(int merchantId);

    Product updateCountStock(int productId, int stock);

    Product adjustStock(int productId, int delta);

    Paged<Product> findAll(int page, int pageSize);
}
