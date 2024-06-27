package com.whitedelay.productshop.wishlist.dto;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class WishlistRequestDto {
    private Member member;
    private Product product;
}