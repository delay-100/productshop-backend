package com.whitedelay.productshop.member.dto;

import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderListResponseDto {

    private Long orderId;
    private String productTitle;
    private LocalDateTime orderDate;
    private OrderStatusEnum orderStatus;
    private int orderPrice;
    private int orderProductCount;

    public static OrderListResponseDto from(Order order, String productTitle, int orderProductCount) {
        return OrderListResponseDto.builder()
                .orderId(order.getOrderId())
                .productTitle(productTitle)
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .orderPrice(order.getOrderPrice())
                .orderProductCount(orderProductCount)
                .build();
    }
}
