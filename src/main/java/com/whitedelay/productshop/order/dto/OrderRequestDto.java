package com.whitedelay.productshop.order.dto;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.order.entity.OrderCardCompanyEnum;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import com.whitedelay.productshop.util.AES256Encoder;
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

    public static OrderRequestDto from(
        OrderProductPayRequestDto requestDto,
        OrderStatusEnum orderStatus,
        AES256Encoder aes256Encoder,
        Member member
    ) {
        return OrderRequestDto.builder()
                .orderDate(LocalDateTime.now())
                .orderStatus(orderStatus)
                .orderPayYN(false)
                .orderShippingFee(requestDto.getOrderShippingFee())
                .orderPrice(requestDto.getOrderPrice())
                .orderCardCompany(requestDto.getOrderCardCompany())
                .orderMemberName(aes256Encoder.encodeString(requestDto.getOrderMemberName()))
                .orderZipCode(requestDto.getOrderZipCode())
                .orderAddress(aes256Encoder.encodeString(requestDto.getOrderAddress()))
                .orderPhone(aes256Encoder.encodeString(requestDto.getOrderPhone()))
                .orderReq(aes256Encoder.encodeString(requestDto.getOrderReq()))
                .member(member)
                .build();
    }
}
