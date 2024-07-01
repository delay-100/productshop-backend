package com.whitedelay.productshop.cart.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CartSimpleInfoRequestDto {
    private long productId;
    private long productOptionId; // nullable
}
