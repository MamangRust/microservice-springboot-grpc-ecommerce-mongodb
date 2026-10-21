package com.sanedge.ecommerce.category.service;

import java.util.List;

import com.sanedge.ecommerce.category.domain.requests.FindAllCategoryRequest;
import com.sanedge.ecommerce.category.domain.responses.CategoryResponse;
import com.sanedge.ecommerce.category.domain.responses.CategoryResponseDeleteAt;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;

public interface CategoryQueryService {
    ApiResponsePagination<List<CategoryResponse>> findAll(FindAllCategoryRequest req);

    ApiResponsePagination<List<CategoryResponseDeleteAt>> findByActive(FindAllCategoryRequest req);

    ApiResponsePagination<List<CategoryResponseDeleteAt>> findByTrashed(FindAllCategoryRequest req);

    ApiResponse<CategoryResponse> findById(Integer categoryId);
}
