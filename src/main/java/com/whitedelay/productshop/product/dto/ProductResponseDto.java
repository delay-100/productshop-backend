package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductResponseDto {
    private Long productId;
    private String productTitle;
    private String productContent;
    private String productStatus;
    private int productWishlistCount;
    private int productPrice;
    private String productCategory;

    public static ProductResponseDto from(Product product) {
        return ProductResponseDto.builder()
                .productId(product.getProductId())
                .productTitle(product.getProductTitle())
                .productContent(product.getProductContent())
                .productStatus(product.getProductStatus())
                .productWishlistCount(product.getProductWishlistCount())
                .productPrice(product.getProductPrice())
                .productCategory(product.getProductCategory().getCategory())
                .build();
    }
}
