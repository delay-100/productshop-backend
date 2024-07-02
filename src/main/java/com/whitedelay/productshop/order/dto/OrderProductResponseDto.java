package com.whitedelay.productshop.order.dto;

import com.whitedelay.productshop.order.entity.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductResponseDto {
    private Long productId;
    private String productTitle;
    private int quantity;
    private Long productOptionId;
    private String productOptionTitle;
    private int productPrice; // 각 제품 가격
    private int productTotalPrice; // 각 제품의 총 결제 금액(수량*가격)

}
