package com.whitedelay.productshop.product.entity;


import com.whitedelay.productshop.product.dto.ProductOptionRequestDto;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Builder(access = AccessLevel.PUBLIC)
@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_option")
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productOptionId;

    @Column(nullable = false)
    private String productOptionName;

    @Column(nullable = false)
    private int productOptionStock;

    @Column(nullable = false)
    private int productOptionPrice;

    @Column(nullable = false)
    private LocalDateTime productStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    public static ProductOption from(ProductOptionRequestDto productOption) {
        return ProductOption.builder()
                .productOptionName(productOption.getProductOptionName())
                .productOptionStock(productOption.getProductOptionStock())
                .productOptionPrice(productOption.getProductOptionPrice())
                .product(productOption.getProduct())
                .build();
    }

    public void setProductOptionStock(int stock) {
        this.productOptionStock = stock;
    }
}
