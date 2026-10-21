package com.sanedge.ecommerce.common.adapter.category;

import java.util.ArrayList;
import java.util.List;

import com.sanedge.ecommerce.common.adapter.model.Category;
import com.sanedge.ecommerce.common.adapter.support.Paged;
import com.sanedge.ecommerce.common.adapter.support.ProtoTime;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.pb.category.CategoryQueryServiceGrpc.CategoryQueryServiceBlockingStub;
import com.sanedge.ecommerce.pb.category.CategoryResponse;
import com.sanedge.ecommerce.pb.category.FindAllCategoryRequest;
import com.sanedge.ecommerce.pb.category.FindByIdCategoryRequest;

public class CategoryAdapter implements CategoryPort {

    private final CategoryQueryServiceBlockingStub client;

    public CategoryAdapter(CategoryQueryServiceBlockingStub client) {
        this.client = client;
    }

    @Override
    public Category findById(int categoryId) {
        var resp = client.findById(FindByIdCategoryRequest.newBuilder().setId(categoryId).build());
        if (!resp.hasData()) {
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        }
        return toModel(resp.getData());
    }

    @Override
    public List<Category> findAllSearch(int page, int pageSize, String search) {
        var resp = client.findAll(FindAllCategoryRequest.newBuilder()
                .setPage(page)
                .setPageSize(pageSize)
                .setSearch(search == null ? "" : search)
                .build());
        return toModels(resp.getDataList());
    }

    @Override
    public Paged<Category> findAll(int page, int pageSize) {
        var resp = client.findAll(FindAllCategoryRequest.newBuilder()
                .setPage(page)
                .setPageSize(pageSize)
                .build());
        List<Category> items = toModels(resp.getDataList());
        int total = resp.hasPagination() ? resp.getPagination().getTotalRecords() : items.size();
        return Paged.of(items, total);
    }

    static Category toModel(CategoryResponse c) {
        if (c == null) {
            return null;
        }
        return new Category(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getSlugCategory(),
                c.getImageCategory(),
                ProtoTime.parse(c.getCreatedAt()),
                ProtoTime.parse(c.getUpdatedAt()));
    }

    static List<Category> toModels(List<CategoryResponse> list) {
        List<Category> out = new ArrayList<>(list.size());
        for (CategoryResponse c : list) {
            out.add(toModel(c));
        }
        return out;
    }
}
