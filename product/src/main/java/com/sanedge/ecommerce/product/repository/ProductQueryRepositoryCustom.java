package com.sanedge.ecommerce.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sanedge.ecommerce.product.models.Product;

public interface ProductQueryRepositoryCustom {
        Page<Product> findProductsByMerchantNative(
                        Integer merchantId,
                        String keyword,
                        Integer categoryId,
                        Integer minPrice,
                        Integer maxPrice,
                        Pageable pageable);

        Page<Product> findProductsByCategoryNameNative(
                        String categoryName,
                        String keyword,
                        Integer minPrice,
                        Integer maxPrice,
                        Pageable pageable);
}
