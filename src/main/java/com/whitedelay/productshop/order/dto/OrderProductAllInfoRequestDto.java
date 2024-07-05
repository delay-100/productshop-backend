package com.whitedelay.productshop.order.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductAllInfoRequestDto {
    private List<OrderProductInfoRequestDto> orderProducts;

}
