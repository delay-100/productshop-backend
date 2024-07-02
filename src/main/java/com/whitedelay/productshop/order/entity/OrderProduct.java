package com.whitedelay.productshop.order.entity;

import com.whitedelay.productshop.order.dto.OrderProductRequestDto;
import com.whitedelay.productshop.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Builder(access = AccessLevel.PRIVATE)
@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_product")
public class OrderProduct extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderProductId;

    @Column(nullable = false)
    private int orderProductQuantity;

    @Column(nullable = false)
    private int orderProductPrice;

    private Long orderProductOptionId; // 현재 productOption에 붙어있는 Option에 대한 값

    private int orderProductOptionPrice;// 현재 productOption에 붙어있는 Option에 가격 대한 값


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public static OrderProduct from(OrderProductRequestDto orderProduct) {
        return OrderProduct.builder()
                .order(orderProduct.getOrder())
                .product(orderProduct.getProduct())
                .orderProductQuantity(orderProduct.getOrderProductQuantity())
                .orderProductPrice(orderProduct.getOrderProductPrice())
                .orderProductOptionId(orderProduct.getOrderProductOptionId())
                .orderProductOptionPrice(orderProduct.getOrderProductOptionPrice())
                .build();
    }


}
