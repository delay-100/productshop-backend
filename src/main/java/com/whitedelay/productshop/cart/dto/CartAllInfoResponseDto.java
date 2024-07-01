package com.whitedelay.productshop.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CartAllInfoResponseDto {

    List<CartInfoResponseDto> cartInfoResponseDtoList;

    private int totalPrice; // 총 결제 금액

    public static CartAllInfoResponseDto from (List<CartInfoResponseDto> cartInfoResponseDtoList, int totalPrice) {
        return CartAllInfoResponseDto.builder()
                .cartInfoResponseDtoList(cartInfoResponseDtoList)
                .totalPrice(totalPrice)
                .build();
    }
}
