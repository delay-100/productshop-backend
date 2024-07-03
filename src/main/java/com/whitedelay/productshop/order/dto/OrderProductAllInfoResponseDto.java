package com.whitedelay.productshop.order.dto;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.security.AES256Encoder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductAllInfoResponseDto {
    // 주문할 상품
    private List<OrderProductResponseDto> orderProducts;

    // 기본으로 사용할 주소 정보 - user에서 빼오기
    private String orderMemberName;
    private int orderZipCode;
    private String orderAddress;
    private String orderPhone;

    // 결제 금액
    private int productTotalPrice;
    private int orderShippingFee;
    private int orderPrice; // 총금액

    public static OrderProductAllInfoResponseDto from(
            Member member,
            AES256Encoder aes256Encoder,
            List<OrderProductResponseDto> orderProducts,
            int productTotalPrice,
            int orderShippingFee,
            int orderPrice
            ) {
        return OrderProductAllInfoResponseDto.builder()
                .orderMemberName(aes256Encoder.decodeString(member.getMemberName()))
                .orderZipCode(member.getZipCode())
                .orderAddress(aes256Encoder.decodeString(member.getAddress()))
                .orderPhone(aes256Encoder.decodeString(member.getPhone()))
                .orderProducts(orderProducts)
                .productTotalPrice(productTotalPrice)
                .orderShippingFee(orderShippingFee)
                .orderPrice(orderPrice)
                .build();
    }
}
