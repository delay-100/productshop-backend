package com.whitedelay.productshop.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderProductPayResponseDto {
    private int totalOrderPrice;
    private int orderShippingFee;
    private int orderPrice;
    private String paymentStatus; // 결제 상태 추가
}
