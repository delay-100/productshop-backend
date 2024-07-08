package com.whitedelay.productshop.order.dto;

import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderProductRequestDto {

    private Order order;
    private Product product;

    private int orderProductQuantity;
    private int orderProductPrice;

    private Long orderProductOptionId;
    private int orderProductOptionPrice;

    public static OrderProductRequestDto from(
//        Order order,
        Product product,
        int orderProductQuantity,
        ProductOption productOption
    ) {
        return OrderProductRequestDto.builder()
//                .order(order)
                .product(product)
                .orderProductQuantity(orderProductQuantity)
                .orderProductPrice(product.getProductPrice()) // 주문 상품 가격 설정
                .orderProductOptionId(productOption.getProductOptionId())
                .orderProductOptionPrice(productOption.getProductOptionPrice())
                .build();
    }
}
