package com.sanedge.ecommerce.common.adapter.category;

import java.util.List;

import com.sanedge.ecommerce.common.adapter.model.Category;
import com.sanedge.ecommerce.common.adapter.support.Paged;

/**
 * Port baca category. Setara {@code category.QueryRepository} +
 * {@code category.BulkRepository} di Go.
 */
public interface CategoryPort {

    /** @throws com.sanedge.ecommerce.common.exception.ResourceNotFoundException bila tidak ada. */
    Category findById(int categoryId);

    List<Category> findAllSearch(int page, int pageSize, String search);

    /** Enumerasi berhalaman untuk backfill statistik. */
    Paged<Category> findAll(int page, int pageSize);
}
