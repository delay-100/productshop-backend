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
    private String productStatus;
    private int productWishlistCount;
    private int productPrice;
    private int productStock;
    private String productCategory;
    private LocalDateTime productStartDate;

    public static WishlistResponseDto from(Wishlist wishlist) {
        return WishlistResponseDto.builder()
                .productId(wishlist.getProduct().getProductId())
                .productTitle(wishlist.getProduct().getProductTitle())
                .productStatus(String.valueOf(wishlist.getProduct().getProductStatus()))
                .productWishlistCount(wishlist.getProduct().getProductWishlistCount())
                .productPrice(wishlist.getProduct().getProductPrice())
                .productStock(wishlist.getProduct().getProductStock())
                .productCategory(String.valueOf(wishlist.getProduct().getProductCategory()))
                .productStartDate(wishlist.getProduct().getProductStartDate())
                .build();
    }
}
