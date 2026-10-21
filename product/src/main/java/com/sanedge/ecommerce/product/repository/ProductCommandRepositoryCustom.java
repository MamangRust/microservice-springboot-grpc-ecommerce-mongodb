package com.sanedge.ecommerce.product.repository;

import com.sanedge.ecommerce.product.models.Product;

public interface ProductCommandRepositoryCustom {
    Product trashed(String productId);

    Product restore(String productId);

    boolean deletePermanent(String productId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();

    Product updateCountInStock(String productId, Integer count);
}
