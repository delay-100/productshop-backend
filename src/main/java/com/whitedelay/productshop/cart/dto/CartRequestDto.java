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
    private Long cartProductOptionId;
    private int cartProductQuantity;
    private Member member;
    private Product product;

    public static CartRequestDto from (Long productOptionId, int quantity, Member member, Product product) {
        return CartRequestDto.builder()
                .cartProductOptionId(productOptionId)
                .cartProductQuantity(quantity)
                .member(member)
                .product(product)
                .build();
    }
}
