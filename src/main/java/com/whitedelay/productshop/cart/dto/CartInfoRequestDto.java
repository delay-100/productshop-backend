package com.whitedelay.productshop.cart.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestParam;

@Builder
@Getter
public class CartInfoRequestDto {
    private long productId;
    private long productOptionId; // nullable
    private int quantity;
}
