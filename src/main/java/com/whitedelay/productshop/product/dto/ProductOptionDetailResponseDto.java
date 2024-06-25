package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.product.entity.ProductOption;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProductOptionDetailResponseDto {
    private Long productOptionId;
    private String productOptionName;
    private int productOptionStock;
    private int productOptionPrice;
    private LocalDateTime productStartDate;

    public static ProductOptionDetailResponseDto from(ProductOption productOption) {
        return ProductOptionDetailResponseDto.builder()
                .productOptionId(productOption.getProductOptionId())
                .productOptionName(productOption.getProductOptionName())
                .productOptionStock(productOption.getProductOptionStock())
                .productOptionPrice(productOption.getProductOptionPrice())
                .productStartDate(productOption.getProductStartDate())
                .build();
    }
}
