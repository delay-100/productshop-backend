package com.whitedelay.productshop.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderProductRequestDto {
    private Long productId;
    private int quantity;
    private Long productOptionId; // nullable, 옵션이 없을 수도 있음
}
