package com.whitedelay.productshop.cart.dto;

import com.whitedelay.productshop.product.entity.ProductOption;
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
    private int productPrice;

    // 상품 옵션 정보 (nullable)
    private Long productOptionId;
    private String productOptionTitle;
    private int productOptionPrice;

    // 상품 총 가격
    private int productTotalPrice;

    public static CartInfoResponseDto from(Long productId, String productTitle, int productPrice, int quantity, ProductOption productOption, int productTotalPrice) {
        return CartInfoResponseDto.builder()
                .productId(productId)
                .productTitle(productTitle)
                .productPrice(productPrice)
                .quantity(quantity)
                .productOptionId(productOption != null ? productOption.getProductOptionId() : 0)
                .productOptionTitle(productOption != null ? productOption.getProductOptionTitle() : null)
                .productOptionPrice(productOption != null ? productOption.getProductOptionPrice() : 0)
                .productTotalPrice(productTotalPrice)
                .build();
    }
}
