package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.product.entity.ProductOption;
import lombok.Getter;

@Getter
public class ProductOptionResponseDto {
    private Long productOptionId;
    private String productOptionName;
    private int productOptionStock;
    private int productOptionPrice;

    public static ProductOptionResponseDto from(ProductOption productOption) {
        return new ProductOptionResponseDto(
                productOption.getProductOptionId(),
                productOption.getProductOptionName(),
                productOption.getProductOptionStock(),
                productOption.getProductOptionPrice()
        );
    }

    public ProductOptionResponseDto(Long productOptionId, String productOptionName, int productOptionStock, int productOptionPrice) {
        this.productOptionId = productOptionId;
        this.productOptionName = productOptionName;
        this.productOptionStock = productOptionStock;
        this.productOptionPrice = productOptionPrice;
    }
}
