package com.sanedge.migration.changelog;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

/**
 * Creates the indexes for the e-commerce collections.
 *
 * <p>Field names mirror the {@code @Field("...")} mappings on the domain models,
 * which persist snake_case names (e.g. {@code user_id}, {@code role_name},
 * {@code slug_product}). Unique indexes replicate the
 * {@code @Indexed(unique = true)} annotations so the guarantees hold even when a
 * service boots with {@code spring.data.mongodb.auto-index-creation=false}.
 *
 * <p>Collections covered: users, roles, user_roles, refresh_tokens, merchants,
 * merchant_details, merchant_business_information, merchant_policies,
 * merchant_certifications_and_awards, merchant_social_media_links, categories,
 * products, banners, sliders, carts, orders, order_items, shipping_addresses,
 * transactions, reviews, review_details. {@code user_roles} is declared by both
 * the role and user modules and {@code review_details} by both review and
 * review_detail — the index set is the union, created exactly once here.
 */
@ChangeUnit(id = "001-create-indexes", order = "001", author = "ecommerce")
public class V001CreateIndexes {

    private static final String USERS = "users";
    private static final String ROLES = "roles";
    private static final String USER_ROLES = "user_roles";
    private static final String REFRESH_TOKENS = "refresh_tokens";
    private static final String MERCHANTS = "merchants";
    private static final String MERCHANT_DETAILS = "merchant_details";
    private static final String MERCHANT_BUSINESS = "merchant_business_information";
    private static final String MERCHANT_POLICIES = "merchant_policies";
    private static final String MERCHANT_AWARDS = "merchant_certifications_and_awards";
    private static final String MERCHANT_SOCIAL = "merchant_social_media_links";
    private static final String CATEGORIES = "categories";
    private static final String PRODUCTS = "products";
    private static final String BANNERS = "banners";
    private static final String SLIDERS = "sliders";
    private static final String CARTS = "carts";
    private static final String ORDERS = "orders";
    private static final String ORDER_ITEMS = "order_items";
    private static final String SHIPPING_ADDRESSES = "shipping_addresses";
    private static final String TRANSACTIONS = "transactions";
    private static final String REVIEWS = "reviews";
    private static final String REVIEW_DETAILS = "review_details";

    @Execution
    public void migrate(MongoTemplate template) {
        // users: unique email; username used for lookups.
        IndexOperations users = template.indexOps(USERS);
        users.ensureIndex(new Index().on("email", Sort.Direction.ASC).unique().named("uq_users_email"));
        users.ensureIndex(new Index().on("username", Sort.Direction.ASC).named("ix_users_username"));

        // roles: unique role_name.
        template.indexOps(ROLES)
                .ensureIndex(new Index().on("role_name", Sort.Direction.ASC).unique().named("uq_roles_role_name"));

        // user_roles: fast lookup by user / role; a user may hold a role only once.
        IndexOperations userRoles = template.indexOps(USER_ROLES);
        userRoles.ensureIndex(new Index().on("user_id", Sort.Direction.ASC).named("ix_user_roles_user_id"));
        userRoles.ensureIndex(new Index().on("role_id", Sort.Direction.ASC).named("ix_user_roles_role_id"));
        userRoles.ensureIndex(new Index().on("user_id", Sort.Direction.ASC)
                .on("role_id", Sort.Direction.ASC).unique().named("uq_user_roles_user_role"));

        // refresh_tokens: unique token; per-user lookup and TTL cleanup on expiry.
        IndexOperations refreshTokens = template.indexOps(REFRESH_TOKENS);
        refreshTokens.ensureIndex(new Index().on("token", Sort.Direction.ASC).unique().named("uq_refresh_tokens_token"));
        refreshTokens.ensureIndex(new Index().on("user_id", Sort.Direction.ASC).named("ix_refresh_tokens_user_id"));
        refreshTokens.ensureIndex(new Index().on("expiry_date", Sort.Direction.ASC)
                .expire(0).named("ttl_refresh_tokens_expiry"));

        // merchants: unique merchant_id; owner and status lookups.
        IndexOperations merchants = template.indexOps(MERCHANTS);
        merchants.ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC).unique().named("uq_merchants_merchant_id"));
        merchants.ensureIndex(new Index().on("user_id", Sort.Direction.ASC).named("ix_merchants_user_id"));
        merchants.ensureIndex(new Index().on("status", Sort.Direction.ASC).named("ix_merchants_status"));

        // merchant_details: per-merchant lookup.
        template.indexOps(MERCHANT_DETAILS)
                .ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC).named("ix_merchant_details_merchant_id"));

        // merchant_business_information: per-merchant lookup.
        template.indexOps(MERCHANT_BUSINESS)
                .ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC)
                        .named("ix_merchant_business_merchant_id"));

        // merchant_policies: per-merchant lookup + policy type filter.
        IndexOperations merchantPolicies = template.indexOps(MERCHANT_POLICIES);
        merchantPolicies.ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC)
                .named("ix_merchant_policies_merchant_id"));
        merchantPolicies.ensureIndex(new Index().on("policy_type", Sort.Direction.ASC)
                .named("ix_merchant_policies_policy_type"));

        // merchant_certifications_and_awards: per-merchant lookup.
        template.indexOps(MERCHANT_AWARDS)
                .ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC).named("ix_merchant_awards_merchant_id"));

        // merchant_social_media_links: per-merchant-detail lookup.
        template.indexOps(MERCHANT_SOCIAL)
                .ensureIndex(new Index().on("merchant_detail_id", Sort.Direction.ASC)
                        .named("ix_merchant_social_merchant_detail_id"));

        // categories: unique slug_category.
        template.indexOps(CATEGORIES)
                .ensureIndex(new Index().on("slug_category", Sort.Direction.ASC).unique()
                        .named("uq_categories_slug_category"));

        // products: unique slug_product; merchant / category lookup.
        IndexOperations products = template.indexOps(PRODUCTS);
        products.ensureIndex(new Index().on("slug_product", Sort.Direction.ASC).unique().named("uq_products_slug_product"));
        products.ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC).named("ix_products_merchant_id"));
        products.ensureIndex(new Index().on("category_id", Sort.Direction.ASC).named("ix_products_category_id"));

        // banners: active-window filter.
        template.indexOps(BANNERS)
                .ensureIndex(new Index().on("is_active", Sort.Direction.ASC).named("ix_banners_is_active"));

        // sliders: indexed by name for ordering lookups.
        template.indexOps(SLIDERS)
                .ensureIndex(new Index().on("name", Sort.Direction.ASC).named("ix_sliders_name"));

        // carts: per-user and per-product lookup.
        IndexOperations carts = template.indexOps(CARTS);
        carts.ensureIndex(new Index().on("user_id", Sort.Direction.ASC).named("ix_carts_user_id"));
        carts.ensureIndex(new Index().on("product_id", Sort.Direction.ASC).named("ix_carts_product_id"));

        // orders: unique order_id; owner and merchant lookups.
        IndexOperations orders = template.indexOps(ORDERS);
        orders.ensureIndex(new Index().on("order_id", Sort.Direction.ASC).unique().named("uq_orders_order_id"));
        orders.ensureIndex(new Index().on("user_id", Sort.Direction.ASC).named("ix_orders_user_id"));
        orders.ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC).named("ix_orders_merchant_id"));

        // order_items: unique order_item_id; per-order lookup.
        IndexOperations orderItems = template.indexOps(ORDER_ITEMS);
        orderItems.ensureIndex(new Index().on("order_item_id", Sort.Direction.ASC).unique()
                .named("uq_order_items_order_item_id"));
        orderItems.ensureIndex(new Index().on("order_id", Sort.Direction.ASC).named("ix_order_items_order_id"));

        // shipping_addresses: unique id; per-order lookup.
        IndexOperations shipping = template.indexOps(SHIPPING_ADDRESSES);
        shipping.ensureIndex(new Index().on("shipping_address_id", Sort.Direction.ASC).unique()
                .named("uq_shipping_addresses_id"));
        shipping.ensureIndex(new Index().on("order_id", Sort.Direction.ASC)
                .named("ix_shipping_addresses_order_id"));

        // transactions: unique transaction_id; order / merchant lookup + status filter.
        IndexOperations transactions = template.indexOps(TRANSACTIONS);
        transactions.ensureIndex(new Index().on("transaction_id", Sort.Direction.ASC).unique()
                .named("uq_transactions_transaction_id"));
        transactions.ensureIndex(new Index().on("order_id", Sort.Direction.ASC).named("ix_transactions_order_id"));
        transactions.ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC)
                .named("ix_transactions_merchant_id"));
        transactions.ensureIndex(new Index().on("payment_status", Sort.Direction.ASC)
                .named("ix_transactions_payment_status"));

        // reviews: unique review_id; per-user / per-product lookup.
        IndexOperations reviews = template.indexOps(REVIEWS);
        reviews.ensureIndex(new Index().on("review_id", Sort.Direction.ASC).unique().named("uq_reviews_review_id"));
        reviews.ensureIndex(new Index().on("user_id", Sort.Direction.ASC).named("ix_reviews_user_id"));
        reviews.ensureIndex(new Index().on("product_id", Sort.Direction.ASC).named("ix_reviews_product_id"));

        // review_details: unique id; per-review lookup.
        IndexOperations reviewDetails = template.indexOps(REVIEW_DETAILS);
        reviewDetails.ensureIndex(new Index().on("review_detail_id", Sort.Direction.ASC).unique()
                .named("uq_review_details_id"));
        reviewDetails.ensureIndex(new Index().on("review_id", Sort.Direction.ASC).named("ix_review_details_review_id"));
    }

    /**
     * Drops the indexes created by {@link #migrate(MongoTemplate)}. Collections
     * and documents are intentionally left untouched — this change unit only
     * owns indexes.
     */
    @RollbackExecution
    public void rollback(MongoTemplate template) {
        dropIndexes(template, USERS, List.of("uq_users_email", "ix_users_username"));
        dropIndexes(template, ROLES, List.of("uq_roles_role_name"));
        dropIndexes(template, USER_ROLES,
                List.of("ix_user_roles_user_id", "ix_user_roles_role_id", "uq_user_roles_user_role"));
        dropIndexes(template, REFRESH_TOKENS,
                List.of("uq_refresh_tokens_token", "ix_refresh_tokens_user_id", "ttl_refresh_tokens_expiry"));
        dropIndexes(template, MERCHANTS,
                List.of("uq_merchants_merchant_id", "ix_merchants_user_id", "ix_merchants_status"));
        dropIndexes(template, MERCHANT_DETAILS, List.of("ix_merchant_details_merchant_id"));
        dropIndexes(template, MERCHANT_BUSINESS, List.of("ix_merchant_business_merchant_id"));
        dropIndexes(template, MERCHANT_POLICIES,
                List.of("ix_merchant_policies_merchant_id", "ix_merchant_policies_policy_type"));
        dropIndexes(template, MERCHANT_AWARDS, List.of("ix_merchant_awards_merchant_id"));
        dropIndexes(template, MERCHANT_SOCIAL, List.of("ix_merchant_social_merchant_detail_id"));
        dropIndexes(template, CATEGORIES, List.of("uq_categories_slug_category"));
        dropIndexes(template, PRODUCTS,
                List.of("uq_products_slug_product", "ix_products_merchant_id", "ix_products_category_id"));
        dropIndexes(template, BANNERS, List.of("ix_banners_is_active"));
        dropIndexes(template, SLIDERS, List.of("ix_sliders_name"));
        dropIndexes(template, CARTS, List.of("ix_carts_user_id", "ix_carts_product_id"));
        dropIndexes(template, ORDERS, List.of("uq_orders_order_id", "ix_orders_user_id", "ix_orders_merchant_id"));
        dropIndexes(template, ORDER_ITEMS, List.of("uq_order_items_order_item_id", "ix_order_items_order_id"));
        dropIndexes(template, SHIPPING_ADDRESSES,
                List.of("uq_shipping_addresses_id", "ix_shipping_addresses_order_id"));
        dropIndexes(template, TRANSACTIONS,
                List.of("uq_transactions_transaction_id", "ix_transactions_order_id",
                        "ix_transactions_merchant_id", "ix_transactions_payment_status"));
        dropIndexes(template, REVIEWS, List.of("uq_reviews_review_id", "ix_reviews_user_id", "ix_reviews_product_id"));
        dropIndexes(template, REVIEW_DETAILS, List.of("uq_review_details_id", "ix_review_details_review_id"));
    }

    private void dropIndexes(MongoTemplate template, String collection, List<String> names) {
        IndexOperations ops = template.indexOps(collection);
        for (IndexInfo index : ops.getIndexInfo()) {
            if (names.contains(index.getName())) {
                ops.dropIndex(index.getName());
            }
        }
    }
}
