package com.sanedge.ecommerce.cart.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.cart.domain.requests.CreateCartRequest;
import com.sanedge.ecommerce.cart.domain.requests.DeleteCartRequest;
import com.sanedge.ecommerce.cart.domain.requests.FindAllCartsRequest;
import com.sanedge.ecommerce.cart.domain.responses.CartResponse;
import com.sanedge.ecommerce.cart.models.Cart;
import com.sanedge.ecommerce.cart.repository.CartCommandRepository;
import com.sanedge.ecommerce.cart.repository.CartQueryRepository;
import com.sanedge.ecommerce.cart.service.CartService;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.common.observability.TracingMetrics;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartImplService implements CartService {
    private final CartQueryRepository cartQueryRepository;
    private final CartCommandRepository cartCommandRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponsePagination<List<CartResponse>> findAll(FindAllCartsRequest req) {
        Attributes attrs = Attributes.builder()
                .put("cart.userId", String.valueOf(req.getUserId()))
                .put("cart.page", String.valueOf(req.getPage()))
                .put("cart.size", String.valueOf(req.getPageSize()))
                .build();

        return tracingMetrics.traceAndMeasure("findAllCarts", "find_all_carts", attrs, () -> {
            log.info("📥 Fetching carts for userId={} | search='{}' | page={} | pageSize={}",
                    req.getUserId(), req.getSearch(), req.getPage(), req.getPageSize());

            Pageable pageable = PageRequest.of(req.getPage() - 1, req.getPageSize(),
                    Sort.by("createdAt").descending());

            try {
                Page<Cart> carts = cartQueryRepository.findCartsByUser(req.getUserId(), req.getSearch(), pageable);

                List<CartResponse> data = carts.getContent().stream()
                        .map(CartResponse::from)
                        .toList();

                return ApiResponsePagination.<List<CartResponse>>builder()
                        .status("success")
                        .message("Cart data fetched successfully")
                        .data(data)
                        .pagination(PaginationMeta.fromSpringPage(carts))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed fetching carts for userId={}", req.getUserId(), e);
                return ApiResponsePagination.<List<CartResponse>>builder()
                        .status("error")
                        .message("Failed to fetch carts")
                        .data(List.of())
                        .pagination(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<CartResponse> createCart(CreateCartRequest req) {
        Attributes attrs = Attributes.builder()
                .put("cart.userId", String.valueOf(req.getUserId()))
                .put("cart.productId", String.valueOf(req.getProductId()))
                .build();

        return tracingMetrics.traceAndMeasure("createCart", "create_cart", attrs, () -> {
            log.info("➕ Creating new cart for userId={} | productId={} | quantity={}",
                    req.getUserId(), req.getProductId(), req.getQuantity());
            try {
                Cart cart = new Cart();
                cart.setUserId(req.getUserId());
                cart.setProductId(req.getProductId());
                cart.setQuantity(req.getQuantity());
                cart.setName("Product " + req.getProductId());
                cart.setPrice(100);
                cart.setImage("default.png");
                cart.setWeight(100);
                cart.setCreatedAt(LocalDateTime.now());

                Cart savedCart = cartCommandRepository.save(cart);

                return ApiResponse.<CartResponse>builder()
                        .status("success")
                        .message("Cart created successfully")
                        .data(CartResponse.from(savedCart))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to create cart for userId={} | productId={}", req.getUserId(),
                        req.getProductId(), e);
                return ApiResponse.<CartResponse>builder()
                        .status("error")
                        .message("Failed to create cart")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deletePermanent(Long cartId) {
        Attributes attrs = Attributes.builder()
                .put("cart.id", String.valueOf(cartId))
                .build();

        return tracingMetrics.traceAndMeasure("deleteCartPermanent", "delete_cart_permanent", attrs, () -> {
            log.info("🗑️ Deleting cart with id={}", cartId);
            try {
                cartCommandRepository.deleteByCartId(String.valueOf(cartId));
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("Cart deleted permanently")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to delete cart with id={}", cartId, e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to delete cart")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAllPermanently(DeleteCartRequest req) {
        return tracingMetrics.traceAndMeasure("deleteCartsPermanently", "delete_carts_permanently", () -> {
            log.info("🗑️ Deleting carts with ids={}", req.getCartIds());
            try {
                List<String> ids = req.getCartIds().stream()
                        .map(String::valueOf)
                        .toList();
                cartCommandRepository.deleteCartsByIds(ids);

                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("Carts deleted permanently")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to delete carts ids={}", req.getCartIds(), e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to delete carts")
                        .data(false)
                        .build();
            }
        });
    }
}
