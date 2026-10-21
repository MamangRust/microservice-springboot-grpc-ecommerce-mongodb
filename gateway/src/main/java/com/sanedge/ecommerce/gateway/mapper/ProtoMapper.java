package com.sanedge.ecommerce.gateway.mapper;

import java.util.List;

import com.sanedge.ecommerce.gateway.dto.responses.auth.TokenResponse;
import com.sanedge.ecommerce.gateway.dto.responses.banner.BannerResponse;
import com.sanedge.ecommerce.gateway.dto.responses.banner.BannerResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.cart.CartResponse;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoriesYearPriceResponse;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoryResponse;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoryResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.merchant.MerchantResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchant.MerchantResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.merchantaward.MerchantAwardResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchantaward.MerchantAwardResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.merchantbusiness.MerchantBusinessResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchantbusiness.MerchantBusinessResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.merchantdetail.MerchantDetailRelationResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchantdetail.MerchantDetailRelationResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.merchantdetail.MerchantDetailResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchantdetail.MerchantDetailResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.merchantpolicy.MerchantPoliciesResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchantpolicy.MerchantPoliciesResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.merchantsociallink.MerchantSocialMediaLinkResponse;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderMonthlyResponse;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderMonthlyTotalRevenueResponse;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderResponse;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderYearlyResponse;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderYearlyTotalRevenueResponse;
import com.sanedge.ecommerce.gateway.dto.responses.orderitem.OrderItemResponse;
import com.sanedge.ecommerce.gateway.dto.responses.product.ProductResponse;
import com.sanedge.ecommerce.gateway.dto.responses.product.ProductResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.reviewdetail.ReviewDetailResponse;
import com.sanedge.ecommerce.gateway.dto.responses.reviewdetail.ReviewDetailResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.reviews.ReviewRelationsDetailResponse;
import com.sanedge.ecommerce.gateway.dto.responses.reviews.ReviewResponse;
import com.sanedge.ecommerce.gateway.dto.responses.reviews.ReviewResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.role.RoleResponse;
import com.sanedge.ecommerce.gateway.dto.responses.role.RoleResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.shipping.ShippingAddressResponse;
import com.sanedge.ecommerce.gateway.dto.responses.shipping.ShippingAddressResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.slider.SliderResponse;
import com.sanedge.ecommerce.gateway.dto.responses.slider.SliderResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionMonthlyAmountFailedResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionMonthlyAmountSuccessResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionMonthlyMethodResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionYearlyAmountFailedResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionYearlyAmountSuccessResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionYearlyMethodResponse;
import com.sanedge.ecommerce.gateway.dto.responses.user.UserResponse;
import com.sanedge.ecommerce.gateway.dto.responses.user.UserResponseDeleteAt;

/**
 * Maps generated protobuf response messages onto the gateway-local REST DTOs so
 * that the JSON contract stays identical to the former monolith. Protobuf types
 * are fully qualified to avoid simple-name collisions with the gateway DTOs.
 */
public final class ProtoMapper {

    private ProtoMapper() {
    }

    private static String str(com.google.protobuf.StringValue value) {
        return value == null ? null : value.getValue();
    }

    // ---- auth ----
    public static TokenResponse token(com.sanedge.ecommerce.pb.auth.TokenResponse p) {
        if (p == null) {
            return null;
        }
        return TokenResponse.builder()
                .access_token(p.getAccessToken())
                .refresh_token(p.getRefreshToken())
                .build();
    }

    // ---- user ----
    public static UserResponse user(com.sanedge.ecommerce.pb.user.UserResponse p) {
        if (p == null) {
            return null;
        }
        return UserResponse.builder()
                .id(p.getId())
                .firstname(p.getFirstname())
                .lastname(p.getLastname())
                .email(p.getEmail())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static UserResponseDeleteAt userDeleteAt(com.sanedge.ecommerce.pb.user.UserResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return UserResponseDeleteAt.builder()
                .id(p.getId())
                .firstname(p.getFirstname())
                .lastname(p.getLastname())
                .email(p.getEmail())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<UserResponse> users(List<com.sanedge.ecommerce.pb.user.UserResponse> list) {
        return list.stream().map(ProtoMapper::user).toList();
    }

    public static List<UserResponseDeleteAt> usersDeleteAt(
            List<com.sanedge.ecommerce.pb.user.UserResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::userDeleteAt).toList();
    }

    // ---- role ----
    public static RoleResponse role(com.sanedge.ecommerce.pb.role.RoleResponse p) {
        if (p == null) {
            return null;
        }
        return RoleResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static RoleResponseDeleteAt roleDeleteAt(com.sanedge.ecommerce.pb.role.RoleResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return RoleResponseDeleteAt.builder()
                .id(p.getId())
                .name(p.getName())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<RoleResponse> roles(List<com.sanedge.ecommerce.pb.role.RoleResponse> list) {
        return list.stream().map(ProtoMapper::role).toList();
    }

    public static List<RoleResponseDeleteAt> rolesDeleteAt(
            List<com.sanedge.ecommerce.pb.role.RoleResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::roleDeleteAt).toList();
    }

    // ---- merchant ----
    public static MerchantResponse merchant(com.sanedge.ecommerce.pb.merchant.MerchantResponse p) {
        if (p == null) {
            return null;
        }
        return MerchantResponse.builder()
                .id((long) p.getId())
                .userId(p.getUserId())
                .name(p.getName())
                .description(p.getDescription())
                .address(p.getAddress())
                .contactEmail(p.getContactEmail())
                .contactPhone(p.getContactPhone())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static MerchantResponseDeleteAt merchantDeleteAt(
            com.sanedge.ecommerce.pb.merchant.MerchantResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return MerchantResponseDeleteAt.builder()
                .id((long) p.getId())
                .userId(p.getUserId())
                .name(p.getName())
                .description(p.getDescription())
                .address(p.getAddress())
                .contactEmail(p.getContactEmail())
                .contactPhone(p.getContactPhone())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<MerchantResponse> merchants(List<com.sanedge.ecommerce.pb.merchant.MerchantResponse> list) {
        return list.stream().map(ProtoMapper::merchant).toList();
    }

    public static List<MerchantResponseDeleteAt> merchantsDeleteAt(
            List<com.sanedge.ecommerce.pb.merchant.MerchantResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::merchantDeleteAt).toList();
    }

    public static MerchantResponseDeleteAt merchantDeleteAtFromResponse(
            com.sanedge.ecommerce.pb.merchant.MerchantResponse p) {
        if (p == null) {
            return null;
        }
        return MerchantResponseDeleteAt.builder()
                .id((long) p.getId())
                .userId(p.getUserId())
                .name(p.getName())
                .description(p.getDescription())
                .address(p.getAddress())
                .contactEmail(p.getContactEmail())
                .contactPhone(p.getContactPhone())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(null)
                .build();
    }

    // ---- merchant award ----
    public static MerchantAwardResponse merchantAward(com.sanedge.ecommerce.pb.merchant_award.MerchantAwardResponse p) {
        if (p == null) {
            return null;
        }
        return MerchantAwardResponse.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .title(p.getTitle())
                .description(p.getDescription())
                .issuedBy(p.getIssuedBy())
                .issueDate(p.getIssueDate())
                .expiryDate(p.getExpiryDate())
                .certificateUrl(p.getCertificateUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static MerchantAwardResponseDeleteAt merchantAwardDeleteAt(
            com.sanedge.ecommerce.pb.merchant_award.MerchantAwardResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return MerchantAwardResponseDeleteAt.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .title(p.getTitle())
                .description(p.getDescription())
                .issuedBy(p.getIssuedBy())
                .issueDate(p.getIssueDate())
                .expiryDate(p.getExpiryDate())
                .certificateUrl(p.getCertificateUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<MerchantAwardResponse> merchantAwards(
            List<com.sanedge.ecommerce.pb.merchant_award.MerchantAwardResponse> list) {
        return list.stream().map(ProtoMapper::merchantAward).toList();
    }

    public static List<MerchantAwardResponseDeleteAt> merchantAwardsDeleteAt(
            List<com.sanedge.ecommerce.pb.merchant_award.MerchantAwardResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::merchantAwardDeleteAt).toList();
    }

    // ---- merchant business ----
    public static MerchantBusinessResponse merchantBusiness(
            com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessResponse p) {
        if (p == null) {
            return null;
        }
        return MerchantBusinessResponse.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .businessType(p.getBusinessType())
                .taxId(p.getTaxId())
                .establishedYear(p.getEstablishedYear())
                .numberOfEmployees(p.getNumberOfEmployees())
                .websiteUrl(p.getWebsiteUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static MerchantBusinessResponseDeleteAt merchantBusinessDeleteAt(
            com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return MerchantBusinessResponseDeleteAt.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .businessType(p.getBusinessType())
                .taxId(p.getTaxId())
                .establishedYear(p.getEstablishedYear())
                .numberOfEmployees(p.getNumberOfEmployees())
                .websiteUrl(p.getWebsiteUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<MerchantBusinessResponse> merchantBusinesses(
            List<com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessResponse> list) {
        return list.stream().map(ProtoMapper::merchantBusiness).toList();
    }

    public static List<MerchantBusinessResponseDeleteAt> merchantBusinessesDeleteAt(
            List<com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::merchantBusinessDeleteAt).toList();
    }

    // ---- merchant policy ----
    public static MerchantPoliciesResponse merchantPolicies(
            com.sanedge.ecommerce.pb.merchant_policy.MerchantPoliciesResponse p) {
        if (p == null) {
            return null;
        }
        return MerchantPoliciesResponse.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .policyType(p.getPolicyType())
                .title(p.getTitle())
                .description(p.getDescription())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static MerchantPoliciesResponseDeleteAt merchantPoliciesDeleteAt(
            com.sanedge.ecommerce.pb.merchant_policy.MerchantPoliciesResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return MerchantPoliciesResponseDeleteAt.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .policyType(p.getPolicyType())
                .title(p.getTitle())
                .description(p.getDescription())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<MerchantPoliciesResponse> merchantPoliciesList(
            List<com.sanedge.ecommerce.pb.merchant_policy.MerchantPoliciesResponse> list) {
        return list.stream().map(ProtoMapper::merchantPolicies).toList();
    }

    public static List<MerchantPoliciesResponseDeleteAt> merchantPoliciesDeleteAtList(
            List<com.sanedge.ecommerce.pb.merchant_policy.MerchantPoliciesResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::merchantPoliciesDeleteAt).toList();
    }

    // ---- merchant detail ----
    public static MerchantDetailResponse merchantDetail(
            com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailResponse p) {
        if (p == null) {
            return null;
        }
        return MerchantDetailResponse.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .displayName(p.getDisplayName())
                .coverImageUrl(p.getCoverImageUrl())
                .logoUrl(p.getLogoUrl())
                .shortDescription(p.getShortDescription())
                .websiteUrl(p.getWebsiteUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static MerchantDetailResponseDeleteAt merchantDetailDeleteAt(
            com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return MerchantDetailResponseDeleteAt.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .displayName(p.getDisplayName())
                .coverImageUrl(p.getCoverImageUrl())
                .logoUrl(p.getLogoUrl())
                .shortDescription(p.getShortDescription())
                .websiteUrl(p.getWebsiteUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static MerchantDetailRelationResponse merchantDetailRelation(
            com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailResponse p) {
        if (p == null) {
            return null;
        }
        return MerchantDetailRelationResponse.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .displayName(p.getDisplayName())
                .coverImageUrl(p.getCoverImageUrl())
                .logoUrl(p.getLogoUrl())
                .shortDescription(p.getShortDescription())
                .websiteUrl(p.getWebsiteUrl())
                .socialMediaLinks(socialLinks(p.getSocialMediaLinksList()))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static MerchantDetailRelationResponseDeleteAt merchantDetailRelationDeleteAt(
            com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return MerchantDetailRelationResponseDeleteAt.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .displayName(p.getDisplayName())
                .coverImageUrl(p.getCoverImageUrl())
                .logoUrl(p.getLogoUrl())
                .shortDescription(p.getShortDescription())
                .websiteUrl(p.getWebsiteUrl())
                .socialMediaLinks(socialLinks(p.getSocialMediaLinksList()))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<MerchantDetailRelationResponse> merchantDetailRelations(
            List<com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailResponse> list) {
        return list.stream().map(ProtoMapper::merchantDetailRelation).toList();
    }

    public static List<MerchantDetailRelationResponseDeleteAt> merchantDetailRelationsDeleteAt(
            List<com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::merchantDetailRelationDeleteAt).toList();
    }

    public static MerchantSocialMediaLinkResponse socialLink(
            com.sanedge.ecommerce.pb.merchant_detail.MerchantSocialMediaLinkResponse p) {
        if (p == null) {
            return null;
        }
        return MerchantSocialMediaLinkResponse.builder()
                .id((long) p.getId())
                .platform(p.getPlatform())
                .url(p.getUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static List<MerchantSocialMediaLinkResponse> socialLinks(
            List<com.sanedge.ecommerce.pb.merchant_detail.MerchantSocialMediaLinkResponse> list) {
        return list.stream().map(ProtoMapper::socialLink).toList();
    }

    // ---- category ----
    public static CategoryResponse category(com.sanedge.ecommerce.pb.category.CategoryResponse p) {
        if (p == null) {
            return null;
        }
        return CategoryResponse.builder()
                .id((long) p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .slugCategory(p.getSlugCategory())
                .imageCategory(p.getImageCategory())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static CategoryResponseDeleteAt categoryDeleteAt(
            com.sanedge.ecommerce.pb.category.CategoryResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return CategoryResponseDeleteAt.builder()
                .id((long) p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .slugCategory(p.getSlugCategory())
                .imageCategory(p.getImageCategory())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<CategoryResponse> categories(List<com.sanedge.ecommerce.pb.category.CategoryResponse> list) {
        return list.stream().map(ProtoMapper::category).toList();
    }

    public static List<CategoryResponseDeleteAt> categoriesDeleteAt(
            List<com.sanedge.ecommerce.pb.category.CategoryResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::categoryDeleteAt).toList();
    }

    public static CategoriesMonthPriceResponse categoryMonthPrice(
            com.sanedge.ecommerce.pb.category.CategoryMonthPriceResponse p) {
        return CategoriesMonthPriceResponse.builder()
                .month(p.getMonth())
                .categoryId((long) p.getCategoryId())
                .categoryName(p.getCategoryName())
                .orderCount((long) p.getOrderCount())
                .itemsSold((long) p.getItemsSold())
                .totalRevenue((long) p.getTotalRevenue())
                .build();
    }

    public static CategoriesYearPriceResponse categoryYearPrice(
            com.sanedge.ecommerce.pb.category.CategoryYearPriceResponse p) {
        return CategoriesYearPriceResponse.builder()
                .year(p.getYear())
                .categoryId((long) p.getCategoryId())
                .categoryName(p.getCategoryName())
                .orderCount((long) p.getOrderCount())
                .itemsSold((long) p.getItemsSold())
                .totalRevenue((long) p.getTotalRevenue())
                .uniqueProductsSold((long) p.getUniqueProductsSold())
                .build();
    }

    public static CategoriesMonthlyTotalPriceResponse categoryMonthTotalPrice(
            com.sanedge.ecommerce.pb.category.CategoriesMonthlyTotalPriceResponse p) {
        return CategoriesMonthlyTotalPriceResponse.builder()
                .year(p.getYear())
                .month(p.getMonth())
                .totalRevenue((long) p.getTotalRevenue())
                .build();
    }

    public static CategoriesYearlyTotalPriceResponse categoryYearTotalPrice(
            com.sanedge.ecommerce.pb.category.CategoriesYearlyTotalPriceResponse p) {
        return CategoriesYearlyTotalPriceResponse.builder()
                .year(p.getYear())
                .totalRevenue((long) p.getTotalRevenue())
                .build();
    }

    public static List<CategoriesMonthPriceResponse> categoryMonthPrices(
            List<com.sanedge.ecommerce.pb.category.CategoryMonthPriceResponse> list) {
        return list.stream().map(ProtoMapper::categoryMonthPrice).toList();
    }

    public static List<CategoriesYearPriceResponse> categoryYearPrices(
            List<com.sanedge.ecommerce.pb.category.CategoryYearPriceResponse> list) {
        return list.stream().map(ProtoMapper::categoryYearPrice).toList();
    }

    public static List<CategoriesMonthlyTotalPriceResponse> categoryMonthTotalPrices(
            List<com.sanedge.ecommerce.pb.category.CategoriesMonthlyTotalPriceResponse> list) {
        return list.stream().map(ProtoMapper::categoryMonthTotalPrice).toList();
    }

    public static List<CategoriesYearlyTotalPriceResponse> categoryYearTotalPrices(
            List<com.sanedge.ecommerce.pb.category.CategoriesYearlyTotalPriceResponse> list) {
        return list.stream().map(ProtoMapper::categoryYearTotalPrice).toList();
    }

    // ---- product ----
    public static ProductResponse product(com.sanedge.ecommerce.pb.product.ProductResponse p) {
        if (p == null) {
            return null;
        }
        return ProductResponse.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .categoryId(p.getCategoryId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .countInStock(p.getCountInStock())
                .brand(p.getBrand())
                .weight(p.getWeight())
                .rating(p.getRating())
                .slugProduct(p.getSlugProduct())
                .imageProduct(p.getImageProduct())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static ProductResponseDeleteAt productDeleteAt(
            com.sanedge.ecommerce.pb.product.ProductResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return ProductResponseDeleteAt.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .categoryId(p.getCategoryId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .countInStock(p.getCountInStock())
                .brand(p.getBrand())
                .weight(p.getWeight())
                .rating(p.getRating())
                .slugProduct(p.getSlugProduct())
                .imageProduct(p.getImageProduct())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<ProductResponse> products(List<com.sanedge.ecommerce.pb.product.ProductResponse> list) {
        return list.stream().map(ProtoMapper::product).toList();
    }

    public static List<ProductResponseDeleteAt> productsDeleteAt(
            List<com.sanedge.ecommerce.pb.product.ProductResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::productDeleteAt).toList();
    }

    // ---- banner ----
    public static BannerResponse banner(com.sanedge.ecommerce.pb.banner.BannerResponse p) {
        if (p == null) {
            return null;
        }
        return BannerResponse.builder()
                .id((long) p.getBannerId())
                .name(p.getName())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .startTime(p.getStartTime())
                .endTime(p.getEndTime())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static BannerResponseDeleteAt bannerDeleteAt(
            com.sanedge.ecommerce.pb.banner.BannerResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return BannerResponseDeleteAt.builder()
                .id((long) p.getBannerId())
                .name(p.getName())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .startTime(p.getStartTime())
                .endTime(p.getEndTime())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<BannerResponse> banners(List<com.sanedge.ecommerce.pb.banner.BannerResponse> list) {
        return list.stream().map(ProtoMapper::banner).toList();
    }

    public static List<BannerResponseDeleteAt> bannersDeleteAt(
            List<com.sanedge.ecommerce.pb.banner.BannerResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::bannerDeleteAt).toList();
    }

    // ---- cart ----
    public static CartResponse cart(com.sanedge.ecommerce.pb.cart.CartResponse p) {
        if (p == null) {
            return null;
        }
        return CartResponse.builder()
                .id((long) p.getId())
                .userId(p.getUserId())
                .productId(p.getProductId())
                .name(p.getName())
                .price(p.getPrice())
                .image(p.getImage())
                .quantity(p.getQuantity())
                .weight(p.getWeight())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static List<CartResponse> carts(List<com.sanedge.ecommerce.pb.cart.CartResponse> list) {
        return list.stream().map(ProtoMapper::cart).toList();
    }

    // ---- slider ----
    public static SliderResponse slider(com.sanedge.ecommerce.pb.slider.SliderResponse p) {
        if (p == null) {
            return null;
        }
        return SliderResponse.builder()
                .id((long) p.getId())
                .name(p.getName())
                .image(p.getImage())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static SliderResponseDeleteAt sliderDeleteAt(
            com.sanedge.ecommerce.pb.slider.SliderResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return SliderResponseDeleteAt.builder()
                .id((long) p.getId())
                .name(p.getName())
                .image(p.getImage())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<SliderResponse> sliders(List<com.sanedge.ecommerce.pb.slider.SliderResponse> list) {
        return list.stream().map(ProtoMapper::slider).toList();
    }

    public static List<SliderResponseDeleteAt> slidersDeleteAt(
            List<com.sanedge.ecommerce.pb.slider.SliderResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::sliderDeleteAt).toList();
    }

    // ---- order ----
    public static OrderResponse order(com.sanedge.ecommerce.pb.order.OrderResponse p) {
        if (p == null) {
            return null;
        }
        return OrderResponse.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .userId(p.getUserId())
                .totalPrice(p.getTotalPrice())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static OrderResponseDeleteAt orderDeleteAt(com.sanedge.ecommerce.pb.order.OrderResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return OrderResponseDeleteAt.builder()
                .id((long) p.getId())
                .merchantId(p.getMerchantId())
                .userId(p.getUserId())
                .totalPrice(p.getTotalPrice())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<OrderResponse> orders(List<com.sanedge.ecommerce.pb.order.OrderResponse> list) {
        return list.stream().map(ProtoMapper::order).toList();
    }

    public static List<OrderResponseDeleteAt> ordersDeleteAt(
            List<com.sanedge.ecommerce.pb.order.OrderResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::orderDeleteAt).toList();
    }

    public static OrderMonthlyResponse orderMonthly(com.sanedge.ecommerce.pb.order.OrderMonthlyResponse p) {
        return OrderMonthlyResponse.builder()
                .month(p.getMonth())
                .orderCount(p.getOrderCount())
                .totalRevenue(p.getTotalRevenue())
                .totalItemsSold(p.getTotalItemsSold())
                .build();
    }

    public static OrderYearlyResponse orderYearly(com.sanedge.ecommerce.pb.order.OrderYearlyResponse p) {
        return OrderYearlyResponse.builder()
                .year(p.getYear())
                .orderCount(p.getOrderCount())
                .totalRevenue(p.getTotalRevenue())
                .totalItemsSold(p.getTotalItemsSold())
                .activeCashiers(p.getActiveCashiers())
                .uniqueProductsSold(p.getUniqueProductsSold())
                .build();
    }

    public static OrderMonthlyTotalRevenueResponse orderMonthlyTotalRevenue(
            com.sanedge.ecommerce.pb.order.OrderMonthlyTotalRevenueResponse p) {
        return OrderMonthlyTotalRevenueResponse.builder()
                .year(p.getYear())
                .month(p.getMonth())
                .totalRevenue(p.getTotalRevenue())
                .build();
    }

    public static OrderYearlyTotalRevenueResponse orderYearlyTotalRevenue(
            com.sanedge.ecommerce.pb.order.OrderYearlyTotalRevenueResponse p) {
        return OrderYearlyTotalRevenueResponse.builder()
                .year(p.getYear())
                .totalRevenue(p.getTotalRevenue())
                .build();
    }

    public static List<OrderMonthlyResponse> orderMonthlies(
            List<com.sanedge.ecommerce.pb.order.OrderMonthlyResponse> list) {
        return list.stream().map(ProtoMapper::orderMonthly).toList();
    }

    public static List<OrderYearlyResponse> orderYearlies(
            List<com.sanedge.ecommerce.pb.order.OrderYearlyResponse> list) {
        return list.stream().map(ProtoMapper::orderYearly).toList();
    }

    public static List<OrderMonthlyTotalRevenueResponse> orderMonthlyTotalRevenues(
            List<com.sanedge.ecommerce.pb.order.OrderMonthlyTotalRevenueResponse> list) {
        return list.stream().map(ProtoMapper::orderMonthlyTotalRevenue).toList();
    }

    public static List<OrderYearlyTotalRevenueResponse> orderYearlyTotalRevenues(
            List<com.sanedge.ecommerce.pb.order.OrderYearlyTotalRevenueResponse> list) {
        return list.stream().map(ProtoMapper::orderYearlyTotalRevenue).toList();
    }

    // ---- order item ----
    public static OrderItemResponse orderItem(com.sanedge.ecommerce.pb.order_item.OrderItemResponse p) {
        if (p == null) {
            return null;
        }
        return OrderItemResponse.builder()
                .id((long) p.getId())
                .orderId(p.getOrderId())
                .productId(p.getProductId())
                .quantity(p.getQuantity())
                .price(p.getPrice())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static List<OrderItemResponse> orderItems(
            List<com.sanedge.ecommerce.pb.order_item.OrderItemResponse> list) {
        return list.stream().map(ProtoMapper::orderItem).toList();
    }

    // ---- review ----
    public static ReviewResponse review(com.sanedge.ecommerce.pb.review.ReviewResponse p) {
        if (p == null) {
            return null;
        }
        return ReviewResponse.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .productId(p.getProductId())
                .name(p.getName())
                .comment(p.getComment())
                .rating(p.getRating())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static ReviewResponseDeleteAt reviewDeleteAt(
            com.sanedge.ecommerce.pb.review.ReviewResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return ReviewResponseDeleteAt.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .productId(p.getProductId())
                .name(p.getName())
                .comment(p.getComment())
                .rating(p.getRating())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<ReviewResponse> reviews(List<com.sanedge.ecommerce.pb.review.ReviewResponse> list) {
        return list.stream().map(ProtoMapper::review).toList();
    }

    public static List<ReviewResponseDeleteAt> reviewsDeleteAt(
            List<com.sanedge.ecommerce.pb.review.ReviewResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::reviewDeleteAt).toList();
    }

    public static ReviewDetailResponse reviewDetailOfReview(com.sanedge.ecommerce.pb.review.ReviewDetailResponse p) {
        if (p == null) {
            return null;
        }
        return ReviewDetailResponse.builder()
                .id(p.getId())
                .type(p.getType())
                .url(p.getUrl())
                .caption(p.getCaption())
                .createdAt(p.getCreatedAt())
                .build();
    }

    public static ReviewRelationsDetailResponse reviewRelationsDetail(
            com.sanedge.ecommerce.pb.review.ReviewsDetailResponse p) {
        if (p == null) {
            return null;
        }
        List<ReviewDetailResponse> details = p.hasReviewDetail()
                ? List.of(reviewDetailOfReview(p.getReviewDetail()))
                : List.of();
        return ReviewRelationsDetailResponse.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .productId(p.getProductId())
                .name(p.getName())
                .comment(p.getComment())
                .rating(p.getRating())
                .reviewDetail(details)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(p.getDeletedAt())
                .build();
    }

    public static List<ReviewRelationsDetailResponse> reviewRelationsDetails(
            List<com.sanedge.ecommerce.pb.review.ReviewsDetailResponse> list) {
        return list.stream().map(ProtoMapper::reviewRelationsDetail).toList();
    }

    // ---- review detail ----
    public static ReviewDetailResponse reviewDetail(com.sanedge.ecommerce.pb.review_detail.ReviewDetailsResponse p) {
        if (p == null) {
            return null;
        }
        return ReviewDetailResponse.builder()
                .id(p.getId())
                .reviewId(p.getReviewId())
                .type(p.getType())
                .url(p.getUrl())
                .caption(p.getCaption())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static ReviewDetailResponseDeleteAt reviewDetailDeleteAt(
            com.sanedge.ecommerce.pb.review_detail.ReviewDetailsResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return ReviewDetailResponseDeleteAt.builder()
                .id(p.getId())
                .reviewId(p.getReviewId())
                .type(p.getType())
                .url(p.getUrl())
                .caption(p.getCaption())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    // ---- shipping ----
    public static ShippingAddressResponse shipping(
            com.sanedge.ecommerce.pb.shipping_address.ShippingResponse p) {
        if (p == null) {
            return null;
        }
        return ShippingAddressResponse.builder()
                .id((long) p.getId())
                .orderId(p.getOrderId())
                .alamat(p.getAlamat())
                .provinsi(p.getProvinsi())
                .negara(p.getNegara())
                .kota(p.getKota())
                .shippingMethod(p.getShippingMethod())
                .shippingCost(p.getShippingCost())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static ShippingAddressResponseDeleteAt shippingDeleteAt(
            com.sanedge.ecommerce.pb.shipping_address.ShippingResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return ShippingAddressResponseDeleteAt.builder()
                .id((long) p.getId())
                .orderId(p.getOrderId())
                .alamat(p.getAlamat())
                .provinsi(p.getProvinsi())
                .negara(p.getNegara())
                .kota(p.getKota())
                .shippingMethod(p.getShippingMethod())
                .shippingCost(p.getShippingCost())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<ShippingAddressResponse> shippings(
            List<com.sanedge.ecommerce.pb.shipping_address.ShippingResponse> list) {
        return list.stream().map(ProtoMapper::shipping).toList();
    }

    public static List<ShippingAddressResponseDeleteAt> shippingsDeleteAt(
            List<com.sanedge.ecommerce.pb.shipping_address.ShippingResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::shippingDeleteAt).toList();
    }

    // ---- transaction ----
    public static TransactionResponse transaction(com.sanedge.ecommerce.pb.transaction.TransactionResponse p) {
        if (p == null) {
            return null;
        }
        return TransactionResponse.builder()
                .id((long) p.getId())
                .orderId(p.getOrderId())
                .merchantId(p.getMerchantId())
                .paymentMethod(p.getPaymentMethod())
                .amount(p.getAmount())
                .paymentStatus(p.getPaymentStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static TransactionResponseDeleteAt transactionDeleteAt(
            com.sanedge.ecommerce.pb.transaction.TransactionResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return TransactionResponseDeleteAt.builder()
                .id((long) p.getId())
                .orderId(p.getOrderId())
                .merchantId(p.getMerchantId())
                .paymentMethod(p.getPaymentMethod())
                .amount(p.getAmount())
                .paymentStatus(p.getPaymentStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<TransactionResponse> transactions(
            List<com.sanedge.ecommerce.pb.transaction.TransactionResponse> list) {
        return list.stream().map(ProtoMapper::transaction).toList();
    }

    public static List<TransactionResponseDeleteAt> transactionsDeleteAt(
            List<com.sanedge.ecommerce.pb.transaction.TransactionResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::transactionDeleteAt).toList();
    }

    public static TransactionResponseDeleteAt transactionDeleteAtFromResponse(
            com.sanedge.ecommerce.pb.transaction.TransactionResponse p) {
        if (p == null) {
            return null;
        }
        return TransactionResponseDeleteAt.builder()
                .id((long) p.getId())
                .orderId(p.getOrderId())
                .merchantId(p.getMerchantId())
                .paymentMethod(p.getPaymentMethod())
                .amount(p.getAmount())
                .paymentStatus(p.getPaymentStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(null)
                .build();
    }

    public static List<TransactionResponseDeleteAt> transactionsDeleteAtFromResponse(
            List<com.sanedge.ecommerce.pb.transaction.TransactionResponse> list) {
        return list.stream().map(ProtoMapper::transactionDeleteAtFromResponse).toList();
    }

    public static TransactionMonthlyAmountSuccessResponse txnMonthlyAmountSuccess(
            com.sanedge.ecommerce.pb.transaction.TransactionMonthlyAmountSuccess p) {
        return TransactionMonthlyAmountSuccessResponse.builder()
                .year(p.getYear())
                .month(p.getMonth())
                .totalSuccess((long) p.getTotalSuccess())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionYearlyAmountSuccessResponse txnYearlyAmountSuccess(
            com.sanedge.ecommerce.pb.transaction.TransactionYearlyAmountSuccess p) {
        return TransactionYearlyAmountSuccessResponse.builder()
                .year(p.getYear())
                .totalSuccess((long) p.getTotalSuccess())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionMonthlyAmountFailedResponse txnMonthlyAmountFailed(
            com.sanedge.ecommerce.pb.transaction.TransactionMonthlyAmountFailed p) {
        return TransactionMonthlyAmountFailedResponse.builder()
                .year(p.getYear())
                .month(p.getMonth())
                .totalFailed((long) p.getTotalFailed())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionYearlyAmountFailedResponse txnYearlyAmountFailed(
            com.sanedge.ecommerce.pb.transaction.TransactionYearlyAmountFailed p) {
        return TransactionYearlyAmountFailedResponse.builder()
                .year(p.getYear())
                .totalFailed((long) p.getTotalFailed())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionMonthlyMethodResponse txnMonthlyMethod(
            com.sanedge.ecommerce.pb.transaction.TransactionMonthlyMethod p) {
        return TransactionMonthlyMethodResponse.builder()
                .month(p.getMonth())
                .paymentMethod(p.getPaymentMethod())
                .totalTransactions((long) p.getTotalTransactions())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionYearlyMethodResponse txnYearlyMethod(
            com.sanedge.ecommerce.pb.transaction.TransactionYearlyMethod p) {
        return TransactionYearlyMethodResponse.builder()
                .year(p.getYear())
                .paymentMethod(p.getPaymentMethod())
                .totalTransactions((long) p.getTotalTransactions())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static List<TransactionMonthlyAmountSuccessResponse> txnMonthlyAmountSuccessList(
            List<com.sanedge.ecommerce.pb.transaction.TransactionMonthlyAmountSuccess> list) {
        return list.stream().map(ProtoMapper::txnMonthlyAmountSuccess).toList();
    }

    public static List<TransactionYearlyAmountSuccessResponse> txnYearlyAmountSuccessList(
            List<com.sanedge.ecommerce.pb.transaction.TransactionYearlyAmountSuccess> list) {
        return list.stream().map(ProtoMapper::txnYearlyAmountSuccess).toList();
    }

    public static List<TransactionMonthlyAmountFailedResponse> txnMonthlyAmountFailedList(
            List<com.sanedge.ecommerce.pb.transaction.TransactionMonthlyAmountFailed> list) {
        return list.stream().map(ProtoMapper::txnMonthlyAmountFailed).toList();
    }

    public static List<TransactionYearlyAmountFailedResponse> txnYearlyAmountFailedList(
            List<com.sanedge.ecommerce.pb.transaction.TransactionYearlyAmountFailed> list) {
        return list.stream().map(ProtoMapper::txnYearlyAmountFailed).toList();
    }

    public static List<TransactionMonthlyMethodResponse> txnMonthlyMethodList(
            List<com.sanedge.ecommerce.pb.transaction.TransactionMonthlyMethod> list) {
        return list.stream().map(ProtoMapper::txnMonthlyMethod).toList();
    }

    public static List<TransactionYearlyMethodResponse> txnYearlyMethodList(
            List<com.sanedge.ecommerce.pb.transaction.TransactionYearlyMethod> list) {
        return list.stream().map(ProtoMapper::txnYearlyMethod).toList();
    }
}
