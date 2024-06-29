package com.whitedelay.productshop.member.dto;

import com.whitedelay.productshop.order.dto.OrderProductResponseDto;
import com.whitedelay.productshop.order.entity.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDetailResponseDto {
    private Long productId;
    private String productTitle;
    private int quantity;
    private Long productOptionId;
    private String productOptionName;
    private int productPrice; // 각 제품 가격
    private int totalPrice; // 각 제품의 총 결제 금액(수량*가격)

    public static OrderProductDetailResponseDto from(OrderProduct orderProduct, String productTitle, String productOptionName) {
        return OrderProductDetailResponseDto.builder()
                .productId(orderProduct.getProduct().getProductId())
                .productTitle(productTitle)
                .quantity(orderProduct.getOrderProductQuantity())
                .productOptionId(orderProduct.getOrderProductOptionId())
                .productOptionName(productOptionName)
                .productPrice(orderProduct.getOrderProductPrice())
                .totalPrice(orderProduct.getOrderProductQuantity() * orderProduct.getOrderProductPrice())
                .build();
    }
}
