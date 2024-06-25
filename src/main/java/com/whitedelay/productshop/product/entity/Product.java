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

    @Column(nullable = false)
    private String productStatus; // 판매중, 판매대기,

    @Column(nullable = false)
    private int productWishlistCount;

    @Column(nullable = false)
    private int productPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategoryEnum productCategory;

    @Column(nullable = false)
    private LocalDateTime productStartDate;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductOption> productOptions;

    public static Product from(ProductRequestDto product) {
        return Product.builder()
                .productTitle(product.getProductTitle())
                .productContent(product.getProductContent())
                .productStatus(product.getProductStatus())
                .productWishlistCount(product.getProductWishlistCount())
                .productPrice(product.getProductPrice())
                .productCategory(ProductCategoryEnum.valueOf(product.getProductCategory()))
                .build();
    }
}
