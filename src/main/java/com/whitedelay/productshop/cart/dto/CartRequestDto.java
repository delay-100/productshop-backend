package com.whitedelay.productshop.cart.dto;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CartRequestDto {
    private long cartProductOptionId;
    private int cartProductStock;
    private Member member;
    private Product product;
}
