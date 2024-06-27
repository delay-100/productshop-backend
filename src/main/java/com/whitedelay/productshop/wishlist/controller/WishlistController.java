package com.whitedelay.productshop.wishlist.controller;

import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.wishlist.dto.WishlistResponseDto;
import com.whitedelay.productshop.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    // wishlist에 특정 상품 추가
    @PostMapping("/wishlist/{productId}")
    public ApiResponse<Boolean> createWishlistWish(
            @CookieValue("${AUTHORIZATION_HEADER}") String userToken,
            @PathVariable long productId
    ) {
        return ApiResponse.createSuccess(wishlistService.createWishlistWish(userToken, productId));
    }

    // 위시리스트에서 특정 상품 삭제
    @DeleteMapping("/wishlist/{productId}")
    public ApiResponse<Boolean> deleteWishlistWish(
            @CookieValue("${AUTHORIZATION_HEADER}") String userToken,
            @PathVariable long productId
    ) {
        return ApiResponse.createSuccess(wishlistService.deleteWishlistWish(userToken, productId));
    }

    // 나의 wishlist 불러오기
    @GetMapping("/wishlist")
    public ApiResponse<Page<WishlistResponseDto>> getAllWishlist(
            @CookieValue("${AUTHORIZATION_HEADER}") String userToken,
            @RequestParam int page,
            @RequestParam int size
        ) {
            return ApiResponse.createSuccess(wishlistService.getAllWishlist(userToken, page, size));
    }
}
