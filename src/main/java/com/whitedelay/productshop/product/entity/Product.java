package com.whitedelay.productshop.product.entity;

import com.whitedelay.productshop.product.dto.ProductRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder(access = AccessLevel.PUBLIC)
@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productTitle;

    @Column(nullable = false)
    private String productContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatusEnum productStatus;

    @Column(nullable = false)
    private int productWishlistCount;

    @Column(nullable = false)
    private int productPrice;

    @Column(nullable = false)
    private int productStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategoryEnum productCategory;

    @Column(nullable = false)
    private LocalDateTime productStartDate;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductOption> productOptions;

    public static Product from(ProductRequestDto product) {
        return Product.builder()
                .productTitle(product.getProductTitle())
                .productContent(product.getProductContent())
                .productStatus(ProductStatusEnum.valueOf(product.getProductStatus().toUpperCase()))
                .productWishlistCount(product.getProductWishlistCount())
                .productPrice(product.getProductPrice())
                .productStock(product.getProductStock())
                .productCategory(ProductCategoryEnum.valueOf(product.getProductCategory().toUpperCase()))
                .productStartDate(product.getProductStartDate())
                .build();
    }

    public void setProductStock(int stock) {
        this.productStock = stock;
    }
}
