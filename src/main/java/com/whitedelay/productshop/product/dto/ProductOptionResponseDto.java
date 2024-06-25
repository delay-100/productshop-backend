package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.product.entity.ProductOption;
import lombok.Getter;

@Getter
public class ProductOptionResponseDto {
    private Long productOptionId;
    private String productOptionName;
    private int productOptionStock;
    private int productOptionPrice;
}
