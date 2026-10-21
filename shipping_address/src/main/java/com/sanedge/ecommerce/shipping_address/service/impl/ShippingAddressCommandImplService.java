package com.sanedge.ecommerce.shipping_address.service.impl;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.shipping_address.domain.requests.CreateShippingAddressRequest;
import com.sanedge.ecommerce.shipping_address.domain.requests.UpdateShippingAddressRequest;
import com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponse;
import com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponseDeleteAt;
import com.sanedge.ecommerce.shipping_address.models.ShippingAddress;
import com.sanedge.ecommerce.shipping_address.repository.ShippingAddressCommandRepository;
import com.sanedge.ecommerce.shipping_address.service.ShippingAddressCommandService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShippingAddressCommandImplService implements ShippingAddressCommandService {

    private final ShippingAddressCommandRepository shippingAddressCommandRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<ShippingAddressResponse> create(CreateShippingAddressRequest request) {
        Attributes attrs = Attributes.builder()
                .put("order.id", String.valueOf(request.getOrderId()))
                .build();

        return tracingMetrics.traceAndMeasure("createShippingAddress", "create_shipping_address", attrs, () -> {
            log.info("🆕 Creating shipping address for orderId={}", request.getOrderId());
            try {
                ShippingAddress address = new ShippingAddress();
                address.setOrderId(request.getOrderId());
                address.setAlamat(request.getAlamat());
                address.setProvinsi(request.getProvinsi());
                address.setKota(request.getKota());
                address.setCourier(request.getCourier());
                address.setShippingMethod(request.getShippingMethod());
                address.setShippingCost(request.getShippingCost());
                address.setNegara(request.getNegara());

                ShippingAddress saved = shippingAddressCommandRepository.save(address);

                log.info("✅ Shipping address created successfully with id={}", saved.getShippingAddressId());

                return ApiResponse.<ShippingAddressResponse>builder()
                        .status("success")
                        .message("✅ Shipping address created successfully!")
                        .data(ShippingAddressResponse.from(saved))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to create shipping address", e);
                return ApiResponse.<ShippingAddressResponse>builder()
                        .status("error")
                        .message(e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<ShippingAddressResponse> update(UpdateShippingAddressRequest request) {
        Attributes attrs = Attributes.builder()
                .put("shipping.id", String.valueOf(request.getShippingId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateShippingAddress", "update_shipping_address", attrs, () -> {
            log.info("🔄 Updating shipping address id={}", request.getShippingId());
            try {
                ShippingAddress address = shippingAddressCommandRepository.findById(String.valueOf(request.getShippingId()))
                        .orElse(null);

                if (address == null) {
                    log.warn("❌ Shipping address not found with id={}", request.getShippingId());
                    return ApiResponse.<ShippingAddressResponse>builder()
                            .status("error")
                            .message("Shipping address not found")
                            .data(null)
                            .build();
                }

                address.setOrderId(request.getOrderId());
                address.setAlamat(request.getAlamat());
                address.setProvinsi(request.getProvinsi());
                address.setKota(request.getKota());
                address.setCourier(request.getCourier());
                address.setShippingMethod(request.getShippingMethod());
                address.setShippingCost(request.getShippingCost());
                address.setNegara(request.getNegara());

                ShippingAddress saved = shippingAddressCommandRepository.save(address);

                log.info("✅ Shipping address updated successfully with id={}", saved.getShippingAddressId());

                return ApiResponse.<ShippingAddressResponse>builder()
                        .status("success")
                        .message("✅ Shipping address updated successfully!")
                        .data(ShippingAddressResponse.from(saved))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to update shipping address id={}", request.getShippingId(), e);
                return ApiResponse.<ShippingAddressResponse>builder()
                        .status("error")
                        .message(e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<ShippingAddressResponseDeleteAt> trash(Integer shippingId) {
        Attributes attrs = Attributes.builder()
                .put("shipping.id", String.valueOf(shippingId))
                .build();

        return tracingMetrics.traceAndMeasure("trashShippingAddress", "trash_shipping_address", attrs, () -> {
            log.info("🗑️ Trashing shipping address id={}", shippingId);
            try {
                ShippingAddress shippingAddress = shippingAddressCommandRepository.trashed(String.valueOf(shippingId));
                return ApiResponse.<ShippingAddressResponseDeleteAt>builder()
                        .status("success")
                        .message("🗑️ Shipping address trashed successfully!")
                        .data(ShippingAddressResponseDeleteAt.from(shippingAddress))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to trash shipping address id={}", shippingId, e);
                return ApiResponse.<ShippingAddressResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash shipping address: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<ShippingAddressResponseDeleteAt> restore(Integer shippingId) {
        Attributes attrs = Attributes.builder()
                .put("shipping.id", String.valueOf(shippingId))
                .build();

        return tracingMetrics.traceAndMeasure("restoreShippingAddress", "restore_shipping_address", attrs, () -> {
            log.info("♻️ Restoring shipping address id={}", shippingId);
            try {
                ShippingAddress shippingAddress = shippingAddressCommandRepository.restore(String.valueOf(shippingId));
                return ApiResponse.<ShippingAddressResponseDeleteAt>builder()
                        .status("success")
                        .message("♻️ Shipping address restored successfully!")
                        .data(ShippingAddressResponseDeleteAt.from(shippingAddress))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore shipping address id={}", shippingId, e);
                return ApiResponse.<ShippingAddressResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore shipping address: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deletePermanently(Integer shippingId) {
        Attributes attrs = Attributes.builder()
                .put("shipping.id", String.valueOf(shippingId))
                .build();

        return tracingMetrics.traceAndMeasure("deleteShippingAddressPermanent", "delete_shipping_address_permanent", attrs,
                () -> {
                    log.info("🧨 Permanently deleting shipping address id={}", shippingId);
                    try {
                        shippingAddressCommandRepository.deletePermanent(String.valueOf(shippingId));
                        return ApiResponse.<Boolean>builder()
                                .status("success")
                                .message("🧨 Shipping address permanently deleted!")
                                .data(true)
                                .build();
                    } catch (Exception e) {
                        log.error("💥 Failed to permanently delete shipping address id={}", shippingId, e);
                        return ApiResponse.<Boolean>builder()
                                .status("error")
                                .message("Failed to permanently delete shipping address: " + e.getMessage())
                                .data(false)
                                .build();
                    }
                });
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        return tracingMetrics.traceAndMeasure("restoreAllShippingAddresses", "restore_all_shipping_addresses", () -> {
            log.info("🔄 Restoring ALL trashed shipping addresses");
            try {
                shippingAddressCommandRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🔄 All shipping addresses restored successfully!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore all shipping addresses", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all shipping addresses: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAllPermanent() {
        return tracingMetrics.traceAndMeasure("deleteAllShippingAddressesPermanent",
                "delete_all_shipping_addresses_permanent", () -> {
                    log.info("💣 Permanently deleting ALL trashed shipping addresses");
                    try {
                        shippingAddressCommandRepository.deleteAllDeleted();
                        return ApiResponse.<Boolean>builder()
                                .status("success")
                                .message("💣 All shipping addresses permanently deleted!")
                                .data(true)
                                .build();
                    } catch (Exception e) {
                        log.error("💥 Failed to delete all shipping addresses", e);
                        return ApiResponse.<Boolean>builder()
                                .status("error")
                                .message("Failed to delete all shipping addresses: " + e.getMessage())
                                .data(false)
                                .build();
                    }
                });
    }
}
