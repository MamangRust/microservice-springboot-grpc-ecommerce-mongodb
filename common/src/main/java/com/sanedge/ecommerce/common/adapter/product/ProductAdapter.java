package com.sanedge.ecommerce.common.adapter.product;

import java.util.ArrayList;
import java.util.List;

import com.sanedge.ecommerce.common.adapter.model.Product;
import com.sanedge.ecommerce.common.adapter.support.AdapterException;
import com.sanedge.ecommerce.common.adapter.support.Paged;
import com.sanedge.ecommerce.common.adapter.support.ProtoTime;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.pb.product.AdjustProductStockRequest;
import com.sanedge.ecommerce.pb.product.ApiResponseProduct;
import com.sanedge.ecommerce.pb.product.FindAllProductMerchantRequest;
import com.sanedge.ecommerce.pb.product.FindAllProductRequest;
import com.sanedge.ecommerce.pb.product.FindByIdProductRequest;
import com.sanedge.ecommerce.pb.product.ProductCommandServiceGrpc.ProductCommandServiceBlockingStub;
import com.sanedge.ecommerce.pb.product.ProductQueryServiceGrpc.ProductQueryServiceBlockingStub;
import com.sanedge.ecommerce.pb.product.ProductResponse;
import com.sanedge.ecommerce.pb.product.UpdateProductCountStockRequest;

public class ProductAdapter implements ProductPort {

    private final ProductQueryServiceBlockingStub query;

    private final ProductCommandServiceBlockingStub command;

    public ProductAdapter(ProductQueryServiceBlockingStub query, ProductCommandServiceBlockingStub command) {
        this.query = query;
        this.command = command;
    }

    @Override
    public Product findById(int productId) {
        var resp = query.findById(FindByIdProductRequest.newBuilder().setId(productId).build());
        if (resp == null || !resp.hasData()) {
            throw new ResourceNotFoundException("Product not found: " + productId);
        }
        return toModel(resp.getData());
    }

    @Override
    public List<Integer> findIdsByMerchant(int merchantId) {
        var resp = query.findByMerchant(FindAllProductMerchantRequest.newBuilder()
                .setMerchantId(merchantId)
                .setPage(1)
                .setPageSize(100_000)
                .build());
        List<Integer> ids = new ArrayList<>(resp.getDataList().size());
        for (ProductResponse p : resp.getDataList()) {
            ids.add(p.getId());
        }
        return ids;
    }

    @Override
    public Product updateCountStock(int productId, int stock) {
        var resp = command.updateProductCountStock(UpdateProductCountStockRequest.newBuilder()
                .setProductId(productId)
                .setStock(stock)
                .build());
        return requireData(resp, "Failed to update product stock: " + productId);
    }

    @Override
    public Product adjustStock(int productId, int delta) {
        var resp = command.adjustStock(AdjustProductStockRequest.newBuilder()
                .setProductId(productId)
                .setDelta(delta)
                .build());
        return requireData(resp, "Failed to adjust product stock: " + productId);
    }

    @Override
    public Paged<Product> findAll(int page, int pageSize) {
        var resp = query.findAll(FindAllProductRequest.newBuilder()
                .setPage(page)
                .setPageSize(pageSize)
                .build());
        List<Product> items = toModels(resp.getDataList());
        int total = resp.hasPagination() ? resp.getPagination().getTotalRecords() : items.size();
        return Paged.of(items, total);
    }

    private static Product requireData(ApiResponseProduct resp, String message) {
        if (resp == null || !resp.hasData()) {
            throw new AdapterException(message);
        }
        return toModel(resp.getData());
    }

    static Product toModel(ProductResponse p) {
        if (p == null) {
            return null;
        }
        return new Product(
                p.getId(),
                p.getMerchantId(),
                p.getCategoryId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getCountInStock(),
                p.getBrand(),
                p.getWeight(),
                p.getRating(),
                p.getSlugProduct(),
                p.getImageProduct(),
                ProtoTime.parse(p.getCreatedAt()),
                ProtoTime.parse(p.getUpdatedAt()));
    }

    static List<Product> toModels(List<ProductResponse> list) {
        List<Product> out = new ArrayList<>(list.size());
        for (ProductResponse p : list) {
            out.add(toModel(p));
        }
        return out;
    }
}
