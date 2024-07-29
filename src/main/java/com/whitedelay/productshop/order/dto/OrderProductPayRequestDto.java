package com.whitedelay.productshop.order.dto;

import com.whitedelay.productshop.order.entity.OrderCardCompanyEnum;
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
    private List<OrderProductResponseDto> orderProductList;

    // 실제로 배송할 주소 정보
    private String orderMemberName;
    private int orderZipCode;
    private String orderAddress;
    private String orderPhone;
    private String orderReq; // 배송 요청사항

    // 결제
    private OrderCardCompanyEnum orderCardCompany;
    private int productTotalPrice;
    private int orderShippingFee;
    private int orderPrice; // 총금액
}
