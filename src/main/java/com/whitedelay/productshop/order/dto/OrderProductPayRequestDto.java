package com.whitedelay.productshop.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductPayRequestDto {

    // 주문할 상품
    private List<OrderProductResponseDto> orderProducts;

    // 실제로 배송할 주소 정보
    private String orderMemberName;
    private String orderZipCode;
    private String orderAddress;
    private String orderPhone;
    private String orderReq; // 배송 요청사항

    // 결제
    private String orderCardCompany;
    private int totalOrderPrice;
    private int orderShippingFee;
    private int orderPrice; // 총금액
}
