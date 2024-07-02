package com.whitedelay.productshop.member.dto;

import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelResponseDto {
    private Long orderId;
    private OrderStatusEnum orderStatus;

    public static OrderCancelResponseDto from(Order order) {
        return OrderCancelResponseDto.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
