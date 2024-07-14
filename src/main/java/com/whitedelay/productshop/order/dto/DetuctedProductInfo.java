package com.whitedelay.productshop.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DetuctedProductInfo {

    private Long productId;
    private Long productOptionId;
    private int productOptionStock;
}
