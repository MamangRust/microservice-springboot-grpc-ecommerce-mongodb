package com.sanedge.ecommerce.transaction.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.sanedge.ecommerce.common.enums.PaymentStatus;
import com.sanedge.ecommerce.common.models.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "transactions")
public class Transaction extends BaseModel {
    @Id
    @Field("transaction_id")
    private String transactionId;

    @Field("order_id")
    private Integer orderId;

    @Field("merchant_id")
    private Integer merchantId;

    @Field("payment_method")
    private String paymentMethod;

    @Field("amount")
    private Integer amount;

    @Field("payment_status")
    private PaymentStatus status = PaymentStatus.PENDING;
}