package com.whitedelay.productshop.product.entity;

import com.whitedelay.productshop.product.dto.ProductRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Entity
//@AllArgsConstructor(access = AccessLevel.PRIVATE)
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productTitle;

    @Column(nullable = false)
    private String productContent;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatusEnum productStatus;

    @Column(nullable = false)
    private int productWishlistCount;

    @Column(nullable = false)
    private int productPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductCategoryEnum productCategory;

    @Column(nullable = false)
    private LocalDateTime productStartDate;

    public static Product from(ProductRequestDto product) {
        return Product.builder()
                .productTitle(product.getProductTitle())
                .productContent(product.getProductContent())
                .productStatus(ProductStatusEnum.valueOf(product.getProductStatus().toUpperCase()))
                .productWishlistCount(product.getProductWishlistCount())
                .productPrice(product.getProductPrice())
                .productCategory(ProductCategoryEnum.valueOf(product.getProductCategory().toUpperCase()))
                .productStartDate(product.getProductStartDate())
                .build();
    }

    public void setProductWishlistCount(int wishlistCount) {
        this.productWishlistCount = wishlistCount;
    }

    public void setProductStatus(ProductStatusEnum productStatusEnum) {
        this.productStatus = productStatusEnum;
    }
}
