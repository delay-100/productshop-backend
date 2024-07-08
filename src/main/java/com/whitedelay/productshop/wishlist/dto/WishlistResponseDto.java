package com.whitedelay.productshop.wishlist.dto;

import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductCategoryEnum;
import com.whitedelay.productshop.product.entity.ProductStatusEnum;
import com.whitedelay.productshop.wishlist.entity.Wishlist;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class WishlistResponseDto {
    private Long productId;
    private String productTitle;
    private ProductStatusEnum productStatus;
    private int productWishlistCount;
    private int productPrice;
    private ProductCategoryEnum productCategory;
    private LocalDateTime productStartDate;

    public static WishlistResponseDto from(Wishlist wishlist) {
        return WishlistResponseDto.builder()
                .productId(wishlist.getProduct().getProductId())
                .productTitle(wishlist.getProduct().getProductTitle())
                .productStatus(wishlist.getProduct().getProductStatus())
                .productWishlistCount(wishlist.getProduct().getProductWishlistCount())
                .productPrice(wishlist.getProduct().getProductPrice())
                .productCategory(wishlist.getProduct().getProductCategory())
                .productStartDate(wishlist.getProduct().getProductStartDate())
                .build();
    }
}
