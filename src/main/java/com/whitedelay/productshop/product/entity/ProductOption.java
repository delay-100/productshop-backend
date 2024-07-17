package com.whitedelay.productshop.product.entity;


import com.whitedelay.productshop.product.dto.ProductOptionRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDateTime;

@Builder(access = AccessLevel.PUBLIC)
@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_option")
public class ProductOption extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productOptionId;

    @Column(nullable = false)
    private String productOptionTitle;

    @Column(nullable = false)
    private int productOptionStock;

    @Column(nullable = false)
    private int productOptionPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    public static ProductOption from(ProductOptionRequestDto productOption, Product product) {
        return ProductOption.builder()
                .productOptionTitle(productOption.getProductOptionTitle())
                .productOptionStock(productOption.getProductOptionStock())
                .productOptionPrice(productOption.getProductOptionPrice())
                .product(product)
                .build();
    }

    public void setProductOptionStock(int stock) {
        this.productOptionStock = stock;
    }
}

