package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import lombok.Builder;
import lombok.Getter;

@Builder
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
}
