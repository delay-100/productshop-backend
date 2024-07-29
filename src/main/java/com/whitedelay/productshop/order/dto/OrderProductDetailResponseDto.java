package com.whitedelay.productshop.order.dto;

import com.whitedelay.productshop.order.entity.OrderProduct;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDetailResponseDto {
    @NotBlank
    private Long productId;
    @NotBlank
    private String productTitle;
    @NotBlank
    private int quantity;
    private Long productOptionId;
    private String productOptionTitle;
    private int productOptionPrice;
    @NotBlank
    private int productPrice; // 각 제품 가격
    @NotBlank
    private int totalPrice; // 각 제품의 총 결제 금액(수량*가격)

    public static OrderProductDetailResponseDto from(OrderProduct orderProduct, String productTitle, String productOptionTitle) {
        return OrderProductDetailResponseDto.builder()
                .productId(orderProduct.getProduct().getProductId())
                .productTitle(productTitle)
                .quantity(orderProduct.getOrderProductQuantity())
                .productOptionId(orderProduct.getOrderProductOptionId())
                .productOptionTitle(productOptionTitle)
                .productOptionPrice(orderProduct.getOrderProductOptionPrice())
                .productPrice(orderProduct.getOrderProductPrice())
                .totalPrice(orderProduct.getOrderProductQuantity() * orderProduct.getOrderProductPrice())
                .build();
    }
}
