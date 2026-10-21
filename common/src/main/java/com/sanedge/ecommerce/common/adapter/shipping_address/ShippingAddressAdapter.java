package com.sanedge.ecommerce.common.adapter.shipping_address;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.adapter.model.ShippingAddress;
import com.sanedge.ecommerce.common.adapter.support.AdapterException;
import com.sanedge.ecommerce.common.adapter.support.ProtoTime;
import com.sanedge.ecommerce.pb.shipping_address.ApiResponseShipping;
import com.sanedge.ecommerce.pb.shipping_address.CreateShippingAddressRequest;
import com.sanedge.ecommerce.pb.shipping_address.FindByIdShippingRequest;
import com.sanedge.ecommerce.pb.shipping_address.ShippingCommandServiceGrpc.ShippingCommandServiceBlockingStub;
import com.sanedge.ecommerce.pb.shipping_address.ShippingQueryServiceGrpc.ShippingQueryServiceBlockingStub;
import com.sanedge.ecommerce.pb.shipping_address.ShippingResponse;
import com.sanedge.ecommerce.pb.shipping_address.UpdateShippingAddressRequest;

public class ShippingAddressAdapter implements ShippingAddressPort {

    private final ShippingQueryServiceBlockingStub query;

    private final ShippingCommandServiceBlockingStub command;

    public ShippingAddressAdapter(ShippingQueryServiceBlockingStub query, ShippingCommandServiceBlockingStub command) {
        this.query = query;
        this.command = command;
    }

    @Override
    public ShippingAddress findById(int shippingId) {
        var resp = query.findById(FindByIdShippingRequest.newBuilder().setId(shippingId).build());
        return toModel(resp.getData());
    }

    @Override
    public ShippingAddress findByOrder(int orderId) {
        var resp = query.findByOrder(FindByIdShippingRequest.newBuilder().setId(orderId).build());
        return toModel(resp.getData());
    }

    @Override
    public ShippingAddress create(CreateData data) {
        var resp = command.createShipping(CreateShippingAddressRequest.newBuilder()
                .setOrderId(data.orderId())
                .setAlamat(data.alamat())
                .setProvinsi(data.provinsi())
                .setKota(data.kota())
                .setNegara(data.negara())
                .setCourier(data.courier())
                .setShippingMethod(data.shippingMethod())
                .setShippingCost(data.shippingCost())
                .build());
        return requireData(resp, "Failed to create shipping address");
    }

    @Override
    public ShippingAddress update(UpdateData data) {
        var resp = command.updateShipping(UpdateShippingAddressRequest.newBuilder()
                .setShippingId(data.shippingId())
                .setOrderId(data.orderId())
                .setAlamat(data.alamat())
                .setProvinsi(data.provinsi())
                .setKota(data.kota())
                .setNegara(data.negara())
                .setCourier(data.courier())
                .setShippingMethod(data.shippingMethod())
                .setShippingCost(data.shippingCost())
                .build());
        return requireData(resp, "Failed to update shipping address");
    }

    @Override
    public Boolean deleteByOrderIdPermanent(int orderId) {
        var resp = command.deleteShippingByOrderPermanent(FindByIdShippingRequest.newBuilder().setId(orderId).build());
        return "success".equals(resp.getStatus());
    }

    @Override
    public Boolean deleteAll() {
        var resp = command.deleteAllShippingPermanent(Empty.getDefaultInstance());
        return "success".equals(resp.getStatus());
    }

    private static ShippingAddress requireData(ApiResponseShipping resp, String message) {
        if (resp == null || !resp.hasData()) {
            throw new AdapterException(message);
        }
        return toModel(resp.getData());
    }

    static ShippingAddress toModel(ShippingResponse s) {
        if (s == null) {
            return null;
        }
        return new ShippingAddress(
                s.getId(),
                s.getOrderId(),
                s.getAlamat(),
                s.getProvinsi(),
                s.getNegara(),
                s.getKota(),
                s.getShippingMethod(),
                s.getShippingCost(),
                ProtoTime.parse(s.getCreatedAt()),
                ProtoTime.parse(s.getUpdatedAt()));
    }
}
