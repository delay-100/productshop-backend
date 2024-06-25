package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.product.entity.Product;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ProductRequestDto {
    private String productTitle;
//    private String product_image;
    private String productContent;
    private String productStatus;
    private int productWishlistCount;
    private int productPrice;
    private String productCategory;
}
