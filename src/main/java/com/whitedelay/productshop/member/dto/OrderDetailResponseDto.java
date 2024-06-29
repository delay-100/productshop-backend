package com.whitedelay.productshop.member.dto;

import com.whitedelay.productshop.order.dto.OrderProductResponseDto;
import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.order.entity.OrderCardCompanyEnum;
import com.whitedelay.productshop.order.entity.OrderProduct;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponseDto {
    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatusEnum orderStatus;
    private int orderShippingFee;
    private OrderCardCompanyEnum orderCardCompany;
    private boolean orderPayYN;
    private int orderPrice;

    private String orderMemberName;
    private String orderZipCode;
    private String orderAddress;
    private String orderPhone;
    private String orderReq;


    private List<OrderProductDetailResponseDto> orderProductDetailResponseDto;

    public static OrderDetailResponseDto from(Order order, List<OrderProductDetailResponseDto> orderProductDetailResponseDto) {
        return OrderDetailResponseDto.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .orderShippingFee(order.getOrderShippingFee())
                .orderPrice(order.getOrderPrice())
                .orderCardCompany(order.getOrderCardCompany())
                .orderPayYN(order.isOrderPayYN())
                .orderMemberName(order.getOrderMemberName())
                .orderZipCode(order.getOrderZipCode())
                .orderAddress(order.getOrderAddress())
                .orderPhone(order.getOrderPhone())
                .orderReq(order.getOrderReq())
                .orderProductDetailResponseDto(orderProductDetailResponseDto)
                .build();
    }

}
