package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.image.dto.ImageResponseDto;
import com.whitedelay.productshop.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ProductListResponseDto {
    private Long productId;
    private String productTitle;
    private String productContent;
    private String productStatus;
    private int productWishlistCount;
    private int productPrice;
    private String productCategory;
    private ImageResponseDto imageResponseDto;

    public static ProductListResponseDto from(Product product, ImageResponseDto imageResponseDto) {
        return ProductListResponseDto.builder()
                .productId(product.getProductId())
                .productTitle(product.getProductTitle())
                .productContent(product.getProductContent())
                .productStatus(product.getProductStatus().getStatus())
                .productWishlistCount(product.getProductWishlistCount())
                .productPrice(product.getProductPrice())
                .productCategory(product.getProductCategory().getCategory())
                .imageResponseDto(imageResponseDto)
                .build();
    }
}
