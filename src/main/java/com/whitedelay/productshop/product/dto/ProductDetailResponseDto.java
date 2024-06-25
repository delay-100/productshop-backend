package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProductDetailResponseDto {
    private Long productId;
    private String productTitle;
    private String productContent;
    private String productStatus;
    private int productWishlistCount;
    private int productPrice;
    private int productStock;
    private String productCategory;
    private List<ProductOptionDetailResponseDto> productOptions;

    public static ProductDetailResponseDto from(Product product) {
        return ProductDetailResponseDto.builder()
                .productId(product.getProductId())
                .productTitle(product.getProductTitle())
                .productContent(product.getProductContent())
                .productStatus(product.getProductStatus().getStatus())
                .productWishlistCount(product.getProductWishlistCount())
                .productPrice(product.getProductPrice())
                .productStock(product.getProductStock())
                .productStock(product.getProductStock())
                .productCategory(product.getProductCategory().getCategory())
                .productOptions(product.getProductOptions().stream()
                        .map(ProductOptionDetailResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
