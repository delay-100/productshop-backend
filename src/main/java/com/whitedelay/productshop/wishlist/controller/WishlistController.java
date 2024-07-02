package com.whitedelay.productshop.wishlist.controller;

import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.wishlist.dto.WishlistWishRequestDto;
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

    private static final String BASE_WISHLIST = "/wishlist";

    /**
     * 위시리스트 상품 추가
     * @param userDetails security의 회원 정보
     * @param wishlistWishRequestDto 추가할 상품 정보
     * @return 상품 추가 성공 여부(T/F)
     */
    @PostMapping("/wishlist")
    public ApiResponse<Boolean> createWishlistWish(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody WishlistWishRequestDto wishlistWishRequestDto
    ) {
        return ApiResponse.createSuccess(wishlistService.createWishlistWish(userDetails.getMember(), wishlistWishRequestDto.getProductId()));
    }

    /**
     * DELETE
     * 위시리스트 상품 삭제
     * @param userDetails security의 회원 정보
     * @param wishlistWishRequestDto 삭제할 상품 정보
     * @return 상품 삭제 성공 여부(T/F)
     */
    @DeleteMapping("/wishlist")
    public ApiResponse<Boolean> deleteWishlistWish(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody WishlistWishRequestDto wishlistWishRequestDto
    ) {
        return ApiResponse.createSuccess(wishlistService.deleteWishlistWish(userDetails.getMember(), wishlistWishRequestDto.getProductId()));
    }

    /**
     * GET
     * 위시리스트 리스트
     * @param userDetails security의 회원 정보
     * @param page 페이지 번호
     * @param size 한 페이지에 띄울 수
     * @return 위시리스트 리스트 DTO
     */
    @GetMapping("/wishlist")
    public ApiResponse<Page<WishlistResponseDto>> getAllWishlist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int page,
            @RequestParam int size
    ) {
            return ApiResponse.createSuccess(wishlistService.getAllWishlist(userDetails.getMember(), page, size));
    }
}
