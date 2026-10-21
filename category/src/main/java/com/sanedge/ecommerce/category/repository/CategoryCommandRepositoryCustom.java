package com.sanedge.ecommerce.category.repository;

import com.sanedge.ecommerce.category.models.Category;

public interface CategoryCommandRepositoryCustom {
    Category trashed(String categoryId);

    Category restore(String categoryId);

    boolean deletePermanent(String categoryId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
