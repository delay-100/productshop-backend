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
    private Long productOptionId;
    private int optionPrice;

}
