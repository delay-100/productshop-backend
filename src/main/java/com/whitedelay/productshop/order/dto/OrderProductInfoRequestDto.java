package com.whitedelay.productshop.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderProductInfoRequestDto {
    private Long productId;
    private int quantity;
    private Long productOptionId; // nullable, 옵션이 없을 수도 있음
    private int optionPrice; // nullable, 없을수도 있음

}
