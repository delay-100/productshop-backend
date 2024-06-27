package com.whitedelay.productshop.wishlist.controller;

import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.wishlist.dto.WishlistResponseDto;
import com.whitedelay.productshop.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    // wishlist에 특정 상품 추가
    @PostMapping("/wishlist/{productId}")
    public ApiResponse<Boolean> createWishlistWish(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable long productId
    ) {
        return ApiResponse.createSuccess(wishlistService.createWishlistWish(userDetails.getMember(), productId));
    }

    // 위시리스트에서 특정 상품 삭제
    @DeleteMapping("/wishlist/{productId}")
    public ApiResponse<Boolean> deleteWishlistWish(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable long productId
    ) {
        return ApiResponse.createSuccess(wishlistService.deleteWishlistWish(userDetails.getMember(), productId));
    }

    // 나의 wishlist 불러오기
    @GetMapping("/wishlist")
    public ApiResponse<Page<WishlistResponseDto>> getAllWishlist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int page,
            @RequestParam int size
        ) {
            return ApiResponse.createSuccess(wishlistService.getAllWishlist(userDetails.getMember(), page, size));
    }
}
