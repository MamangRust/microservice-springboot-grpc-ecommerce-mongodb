package com.sanedge.ecommerce.category.service.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.category.domain.requests.CreateCategoryRequest;
import com.sanedge.ecommerce.category.domain.requests.UpdateCategoryRequest;
import com.sanedge.ecommerce.category.domain.responses.CategoryResponse;
import com.sanedge.ecommerce.category.domain.responses.CategoryResponseDeleteAt;
import com.sanedge.ecommerce.common.utils.Ids;
import com.sanedge.ecommerce.category.models.Category;
import com.sanedge.ecommerce.category.repository.CategoryCommandRepository;
import com.sanedge.ecommerce.category.repository.CategoryQueryRepository;
import com.sanedge.ecommerce.category.service.CategoryCommandService;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.observability.TracingMetrics;

import io.opentelemetry.api.common.Attributes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryCommandImplService implements CategoryCommandService {
    private final CategoryCommandRepository categoryCommandRepository;
    private final CategoryQueryRepository categoryQueryRepository;
    private final Validator validator;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<CategoryResponse> createCategory(CreateCategoryRequest req) {
        Attributes attrs = Attributes.builder()
                .put("category.name", req.getName() != null ? req.getName() : "")
                .build();

        return tracingMetrics.traceAndMeasure("createCategory", "create_category", attrs, () -> {
            try {
                log.info("🆕 Creating category: {}", req.getName());

                Category category = new Category();
                category.setName(req.getName());
                category.setDescription(req.getDescription());
                category.setSlugCategory(req.getSlugCategory());
                category.setImageCategory(req.getImageCategory());

                Category saved = categoryCommandRepository.save(category);

                return ApiResponse.<CategoryResponse>builder()
                        .status("success")
                        .message("Category created successfully")
                        .data(CategoryResponse.from(saved))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to create category: {}", e.getMessage(), e);
                return ApiResponse.<CategoryResponse>builder()
                        .status("error")
                        .message("Internal error while creating category")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<CategoryResponse> updateCategory(UpdateCategoryRequest req) {
        Attributes attrs = Attributes.builder()
                .put("category.id", String.valueOf(req.getCategoryId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateCategory", "update_category", attrs, () -> {
            try {
                log.info("✏️ Updating category ID: {}", req.getCategoryId());
                validateRequest(req);

                Category category = categoryQueryRepository.findCategoryById(String.valueOf(req.getCategoryId()))
                        .orElse(null);
                if (category == null) {
                    return ApiResponse.<CategoryResponse>builder()
                            .status("error")
                            .message("Category not found")
                            .build();
                }

                category.setName(req.getName());
                category.setDescription(req.getDescription());
                category.setSlugCategory(req.getSlugCategory());
                category.setImageCategory(req.getImageCategory());

                Category updated = categoryCommandRepository.save(category);

                return ApiResponse.<CategoryResponse>builder()
                        .status("success")
                        .message("Category updated successfully")
                        .data(CategoryResponse.from(updated))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to update category: {}", e.getMessage(), e);
                return ApiResponse.<CategoryResponse>builder()
                        .status("error")
                        .message("Internal error while updating category")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<CategoryResponseDeleteAt> trashedCategory(Integer categoryId) {
        Attributes attrs = Attributes.builder()
                .put("category.id", String.valueOf(categoryId))
                .build();

        return tracingMetrics.traceAndMeasure("trashCategory", "trash_category", attrs, () -> {
            try {
                log.info("🗑️ Trashing category id={}", categoryId);
                Category category = categoryCommandRepository.trashed(String.valueOf(categoryId));

                return ApiResponse.<CategoryResponseDeleteAt>builder()
                        .status("success")
                        .message("🗑️ Category trashed successfully!")
                        .data(CategoryResponseDeleteAt.from(category))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to trash category id={}: {}", categoryId, e.getMessage(), e);
                return ApiResponse.<CategoryResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash category")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<CategoryResponseDeleteAt> restoreCategory(Integer categoryId) {
        Attributes attrs = Attributes.builder()
                .put("category.id", String.valueOf(categoryId))
                .build();

        return tracingMetrics.traceAndMeasure("restoreCategory", "restore_category", attrs, () -> {
            try {
                log.info("♻️ Restoring category id={}", categoryId);
                Category category = categoryCommandRepository.restore(String.valueOf(categoryId));

                return ApiResponse.<CategoryResponseDeleteAt>builder()
                        .status("success")
                        .message("♻️ Category restored successfully!")
                        .data(CategoryResponseDeleteAt.from(category))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to restore category id={}: {}", categoryId, e.getMessage(), e);
                return ApiResponse.<CategoryResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore category")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteCategoryPermanent(Integer categoryId) {
        Attributes attrs = Attributes.builder()
                .put("category.id", String.valueOf(categoryId))
                .build();

        return tracingMetrics.traceAndMeasure("deleteCategoryPermanent", "delete_category_permanent", attrs, () -> {
            try {
                log.info("🧨 Permanently deleting category id={}", categoryId);
                categoryCommandRepository.deletePermanent(String.valueOf(categoryId));

                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🧨 Category permanently deleted!")
                        .data(true)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to permanently delete category id={}: {}", categoryId, e.getMessage(), e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete category")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAllCategories() {
        return tracingMetrics.traceAndMeasure("restoreAllCategories", "restore_all_categories", () -> {
            try {
                log.info("🔄 Restoring ALL trashed categories");
                categoryCommandRepository.restoreAllDeleted();

                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🔄 All categories restored successfully!")
                        .data(true)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to restore all categories: {}", e.getMessage(), e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all categories")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAllCategoriesPermanent() {
        return tracingMetrics.traceAndMeasure("deleteAllCategoriesPermanent", "delete_all_categories_permanent", () -> {
            try {
                log.info("💣 Permanently deleting ALL trashed categories");
                categoryCommandRepository.deleteAllDeleted();

                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("💣 All categories permanently deleted!")
                        .data(true)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to delete all categories: {}", e.getMessage(), e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to delete all categories")
                        .data(false)
                        .build();
            }
        });
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.warn("⚠️ Validation failed: {}", sb);
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }
}
