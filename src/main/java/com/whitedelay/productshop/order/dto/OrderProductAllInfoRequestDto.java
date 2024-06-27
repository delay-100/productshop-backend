package com.whitedelay.productshop.order.dto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderProductAllInfoRequestDto {
    private List<OrderProductRequestDto> orderProducts;

}
