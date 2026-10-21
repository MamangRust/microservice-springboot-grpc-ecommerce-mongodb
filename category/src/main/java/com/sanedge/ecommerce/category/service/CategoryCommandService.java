package com.sanedge.ecommerce.category.service;

import com.sanedge.ecommerce.category.domain.requests.CreateCategoryRequest;
import com.sanedge.ecommerce.category.domain.requests.UpdateCategoryRequest;
import com.sanedge.ecommerce.category.domain.responses.CategoryResponse;
import com.sanedge.ecommerce.category.domain.responses.CategoryResponseDeleteAt;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;

public interface CategoryCommandService {
    ApiResponse<CategoryResponse> createCategory(CreateCategoryRequest req);

    ApiResponse<CategoryResponse> updateCategory(UpdateCategoryRequest req);

    ApiResponse<CategoryResponseDeleteAt> trashedCategory(Integer categoryId);

    ApiResponse<CategoryResponseDeleteAt> restoreCategory(Integer categoryId);

    ApiResponse<Boolean> deleteCategoryPermanent(Integer categoryId);

    ApiResponse<Boolean> restoreAllCategories();

    ApiResponse<Boolean> deleteAllCategoriesPermanent();
}
