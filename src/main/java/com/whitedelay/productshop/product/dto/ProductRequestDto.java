package com.whitedelay.productshop.product.dto;

import lombok.*;

import java.time.LocalDateTime;


@Setter
@Getter
public class ProductRequestDto {
    private String productTitle;
    private String productContent;
    private String productStatus;
    private int productWishlistCount;
    private int productPrice;
    private String productCategory;
    private LocalDateTime productStartDate;
}
