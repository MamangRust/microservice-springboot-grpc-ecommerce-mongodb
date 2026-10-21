package com.sanedge.ecommerce.product.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.product.domain.requests.CreateProductRequest;
import com.sanedge.ecommerce.product.domain.requests.UpdateProductRequest;
import com.sanedge.ecommerce.product.domain.responses.ProductResponse;
import com.sanedge.ecommerce.product.domain.responses.ProductResponseDeleteAt;

public interface ProductCommandService {
    ApiResponse<ProductResponse> createProduct(CreateProductRequest req);

    ApiResponse<ProductResponse> updateProduct(UpdateProductRequest req);

    ApiResponse<ProductResponseDeleteAt> trashedProduct(Integer productId);

    ApiResponse<ProductResponseDeleteAt> restoreProduct(Integer productId);

    ApiResponse<Boolean> deleteProductPermanent(Integer productId);

    ApiResponse<Boolean> restoreAllProducts();

    ApiResponse<Boolean> deleteAllProductsPermanent();
}
