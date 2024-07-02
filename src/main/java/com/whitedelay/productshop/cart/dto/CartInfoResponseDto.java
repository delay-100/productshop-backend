package com.whitedelay.productshop.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CartInfoResponseDto {
    // 상품 정보
    private Long productId;
    private String productTitle;
    private int quantity;

    // 상품 옵션 정보 (nullable)
    private Long productOptionId;
    private String productOptionName;
    private int productOptionPrice;
    private int productOptionStock;


}
