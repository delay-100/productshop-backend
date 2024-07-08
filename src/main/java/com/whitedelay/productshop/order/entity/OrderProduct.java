package com.whitedelay.productshop.order.entity;

import com.whitedelay.productshop.order.dto.OrderProductRequestDto;
import com.whitedelay.productshop.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_product")
public class OrderProduct extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderProductId;

    @Column(nullable = false)
    private int orderProductQuantity;

    @Column(nullable = false)
    private int orderProductPrice;

    @Column(nullable = false)
    private Long orderProductOptionId;

    @Column(nullable = false)
    private int orderProductOptionPrice;

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

    public void setOrder(Order order) {
        this.order = order;
    }

}
