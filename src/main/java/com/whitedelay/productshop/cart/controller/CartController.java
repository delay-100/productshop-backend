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

    private static final String BASE_CART = "/cart";

    /**
     * POST
     * 장바구니에 넣기
     * @param userDetails security의 회원 정보
     * @param cartInfoRequestDto 장바구니 담는 객체
     * @return 담은 장바구니 DTO
     */
    @PostMapping(BASE_CART)
    public ApiResponse<CartInfoResponseDto> createCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CartInfoRequestDto cartInfoRequestDto
    ) {
        return ApiResponse.createSuccess(cartService.createCart(userDetails.getMember(), cartInfoRequestDto.getProductId(), cartInfoRequestDto.getProductOptionId(), cartInfoRequestDto.getQuantity()));
    }

    /**
     * DELETE
     * 장바구니에서 상품 삭제
     * @param userDetails security의 회원 정보
     * @param cartSimpleInfoRequestDto 장바구니에서 삭제할 정보
     * @return 상품 삭제 여부(T/F)
     */
    @DeleteMapping(BASE_CART)
    public ApiResponse<Boolean> deleteCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CartSimpleInfoRequestDto cartSimpleInfoRequestDto
    ) {
        return ApiResponse.createSuccess(cartService.deleteCart(userDetails.getMember(), cartSimpleInfoRequestDto.getProductId(), cartSimpleInfoRequestDto.getProductOptionId()));
    }

    /**
     * GET
     * 장바구니 모든 상품 리스트
     * @param userDetails security의 회원 정보
     * @return 장바구니 모든 상품 리스트 DTO
     */
    @GetMapping(BASE_CART)
    public ApiResponse<CartAllInfoResponseDto> getCartAllInfo(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    )   {
        return ApiResponse.createSuccess(cartService.getCartAllInfo(userDetails.getMember()));
    }
}
