package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.product.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOptionRequestDto {
    private String productOptionName;
    private int productOptionStock;
    private int productOptionPrice;
    private long productId;
    private Product product;
}
