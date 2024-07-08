package com.whitedelay.productshop.cart.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CartInfoRequestDto {
    private Long productId;
    private Long productOptionId;
    private int quantity;
}
