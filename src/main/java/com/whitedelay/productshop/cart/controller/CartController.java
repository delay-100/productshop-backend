package com.whitedelay.productshop.cart.controller;

import com.whitedelay.productshop.cart.dto.CartAllInfoResponseDto;
import com.whitedelay.productshop.cart.dto.CartInfoRequestDto;
import com.whitedelay.productshop.cart.dto.CartInfoResponseDto;
import com.whitedelay.productshop.cart.dto.CartSimpleInfoRequestDto;
import com.whitedelay.productshop.cart.service.CartService;
import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/cart")
    public ApiResponse<CartInfoResponseDto> createCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CartInfoRequestDto cartInfoRequestDto
    ) {
        return cartService.createCart(userDetails.getMember(), cartInfoRequestDto.getProductId(), cartInfoRequestDto.getProductOptionId(), cartInfoRequestDto.getQuantity());
    }

    @DeleteMapping("/cart")
    public ApiResponse<Boolean> deleteCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CartSimpleInfoRequestDto cartSimpleInfoRequestDto
    ) {
        return cartService.deleteCart(userDetails.getMember(), cartSimpleInfoRequestDto.getProductId(), cartSimpleInfoRequestDto.getProductOptionId());
    }

    @GetMapping("/cart")
    public ApiResponse<CartAllInfoResponseDto> getCartAllInfo(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    )   {
        return cartService.getCartAllInfo(userDetails.getMember());
    }
}
