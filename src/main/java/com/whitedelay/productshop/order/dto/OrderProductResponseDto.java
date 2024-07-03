package com.whitedelay.productshop.order.dto;

import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductResponseDto {
    private Long productId;
    private String productTitle;
    private int quantity;
    private Long productOptionId;
    private String productOptionTitle;
    private int productPrice; // 각 제품 가격
    private int productOptionPrice; // 각 제품의 옵션 가격
    private int productTotalPrice; // 각 제품의 총 결제 금액(수량*가격)

    public static OrderProductResponseDto from(
            Product product,
            int quantity,
            ProductOption productOption,
            int productPrice,
            int productTotalPrice
    ) {
        return OrderProductResponseDto.builder()
                .productId(product.getProductId())
                .productTitle(product.getProductTitle())
                .quantity(quantity)
                .productOptionId(productOption != null ? productOption.getProductOptionId() : null)
                .productOptionTitle(productOption != null ? productOption.getProductOptionTitle() : null)
                .productPrice(productPrice)
                .productOptionPrice(productOption != null ? productOption.getProductOptionPrice() : 0)
                .productTotalPrice(productTotalPrice)
                .build();
    }
}
