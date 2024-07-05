package com.whitedelay.productshop.order.dto;

import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderProductPayResponseDto {
    private int totalOrderPrice;
    private int orderShippingFee;
    private int orderPrice;
    private OrderStatusEnum paymentStatus; // 결제 상태 추가

    public static OrderProductPayResponseDto from(
            int totalOrderPrice,
            int orderShippingFee,
            int orderPrice,
            OrderStatusEnum paymentStatus
    ) {
        return OrderProductPayResponseDto.builder()
                .totalOrderPrice(totalOrderPrice)
                .orderShippingFee(orderShippingFee)
                .orderPrice(orderPrice)
                .paymentStatus(paymentStatus)
                .build();
    }
}
