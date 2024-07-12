package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.product.entity.ProductOption;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProductOptionDetailResponseDto {
    private Long productOptionId;
    private String productOptionTitle;
    private int productOptionStock;
    private int productOptionPrice;

    public static ProductOptionDetailResponseDto from(ProductOption productOption) {
        return ProductOptionDetailResponseDto.builder()
                .productOptionId(productOption.getProductOptionId())
                .productOptionTitle(productOption.getProductOptionTitle())
                .productOptionStock(productOption.getProductOptionStock())
                .productOptionPrice(productOption.getProductOptionPrice())
                .build();
    }
}
