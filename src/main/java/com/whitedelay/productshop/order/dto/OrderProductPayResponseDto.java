package com.whitedelay.productshop.order.dto;

import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderProductPayResponseDto {
    private int productTotalPrice;
    private int orderShippingFee;
    private int orderPrice;
    private OrderStatusEnum paymentStatus; // 결제 상태 추가

    public static OrderProductPayResponseDto from(
            int productTotalPrice,
            int orderShippingFee,
            int orderPrice,
            OrderStatusEnum paymentStatus
    ) {
        return OrderProductPayResponseDto.builder()
                .productTotalPrice(productTotalPrice)
                .orderShippingFee(orderShippingFee)
                .orderPrice(orderPrice)
                .paymentStatus(paymentStatus)
                .build();
    }
}
