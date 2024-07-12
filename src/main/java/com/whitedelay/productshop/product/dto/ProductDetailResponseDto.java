package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.image.dto.ImageResponseDto;
import com.whitedelay.productshop.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductDetailResponseDto {
    private Long productId;
    private String productTitle;
    private String productContent;
    private String productStatus;
    private int productWishlistCount;
    private int productPrice;
    private String productCategory;
    private List<ProductOptionDetailResponseDto> productOptionList;
    private List<ImageResponseDto> imageResponseDtoList;

    public static ProductDetailResponseDto from(Product product, List<ProductOptionDetailResponseDto> productOptionList, List<ImageResponseDto> imageResponseDtoList) {
        return ProductDetailResponseDto.builder()
                .productId(product.getProductId())
                .productTitle(product.getProductTitle())
                .productContent(product.getProductContent())
                .productStatus(product.getProductStatus().getStatus())
                .productWishlistCount(product.getProductWishlistCount())
                .productPrice(product.getProductPrice())
                .productCategory(product.getProductCategory().getCategory())
                .productOptionList(productOptionList)
                .imageResponseDtoList(imageResponseDtoList)
                .build();
    }
}
