package com.whitedelay.productshop.member.dto;

import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.order.entity.OrderCardCompanyEnum;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import com.whitedelay.productshop.util.AES256Encoder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private int orderZipCode;
    private String orderAddress;
    private String orderPhone;
    private String orderReq;


    private List<OrderProductDetailResponseDto> orderProductDetailResponseDto;

    public static OrderDetailResponseDto from(Order order, List<OrderProductDetailResponseDto> orderProductDetailResponseDto, AES256Encoder aes256Encoder) {
        return OrderDetailResponseDto.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .orderShippingFee(order.getOrderShippingFee())
                .orderPrice(order.getOrderPrice())
                .orderCardCompany(order.getOrderCardCompany())
                .orderPayYN(order.isOrderPayYN())
                .orderMemberName(aes256Encoder.decodeString(order.getOrderMemberName()))
                .orderZipCode(order.getOrderZipCode())
                .orderAddress(aes256Encoder.decodeString(order.getOrderAddress()))
                .orderPhone(aes256Encoder.decodeString(order.getOrderPhone()))
                .orderReq(aes256Encoder.decodeString(order.getOrderReq()))
                .orderProductDetailResponseDto(orderProductDetailResponseDto)
                .build();
    }

}
