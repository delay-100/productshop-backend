package com.whitedelay.productshop.order.dto;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.order.entity.OrderCardCompanyEnum;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
public class OrderRequestDto {
    private LocalDateTime orderDate;
    private OrderStatusEnum orderStatus;
    private int orderShippingFee;
    private int orderPrice;
    private OrderCardCompanyEnum orderCardCompany;
    private boolean orderPayYN;

    // 결제 완료 시 사용할 주소 정보
    private String orderMemberName;
    private int orderZipCode;
    private String orderAddress;
    private String orderPhone;
    private String orderReq;

    private Member member;
}
