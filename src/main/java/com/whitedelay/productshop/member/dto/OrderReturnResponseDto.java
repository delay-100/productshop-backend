package com.whitedelay.productshop.member.dto;

import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderReturnResponseDto {
    private Long orderId;
    private OrderStatusEnum orderStatus;

    public static OrderReturnResponseDto from(Order order) {
        return OrderReturnResponseDto.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
