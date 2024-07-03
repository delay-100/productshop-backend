package com.whitedelay.productshop.wishlist.controller;

import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.wishlist.dto.WishlistWishRequestDto;
import com.whitedelay.productshop.wishlist.dto.WishlistResponseDto;
import com.whitedelay.productshop.wishlist.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WishlistControllerTest {

    @InjectMocks
    private WishlistController wishlistController;

    @Mock
    private WishlistService wishlistService;

    @Mock
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        when(userDetails.getMember()).thenReturn(null); // 테스트용 사용자 정보 설정
    }

    @Test
    @DisplayName("위시리스트 상품 추가")
    void createWishlistWish_Success() {
        // Given
        WishlistWishRequestDto wishlistWishRequestDto = WishlistWishRequestDto.builder()
                .productId(1L)
                .build();

        when(wishlistService.createWishlistWish(any(), any(Long.class)))
                .thenReturn(true);

        // When
        ApiResponse<Boolean> response = wishlistController.createWishlistWish(userDetails, wishlistWishRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    @DisplayName("위시리스트 상품 삭제")
    void deleteWishlistWish_Success() {
        // Given
        WishlistWishRequestDto wishlistWishRequestDto = WishlistWishRequestDto.builder()
                .productId(1L)
                .build();

        when(wishlistService.deleteWishlistWish(any(), any(Long.class)))
                .thenReturn(true);

        // When
        ApiResponse<Boolean> response = wishlistController.deleteWishlistWish(userDetails, wishlistWishRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    @DisplayName("위시리스트 조회")
    void getAllWishlist_Success() {
        // Given
        WishlistResponseDto wishlistResponseDto1 = WishlistResponseDto.builder()
                .productId(1L)
                .productTitle("샘플 상품1")
                .productStatus("AVAILABLE")
                .productWishlistCount(100)
                .productPrice(200)
                .productStock(50)
                .productCategory("FOOD")
                .productStartDate(LocalDateTime.now())
                .build();

        WishlistResponseDto wishlistResponseDto2 = WishlistResponseDto.builder()
                .productId(2L)
                .productTitle("샘플 상품2")
                .productStatus("AVAILABLE")
                .productWishlistCount(50)
                .productPrice(300)
                .productStock(30)
                .productCategory("BOOKS")
                .productStartDate(LocalDateTime.now())
                .build();

        Page<WishlistResponseDto> wishlistResponseDtoPage = new PageImpl<>(
                Arrays.asList(wishlistResponseDto1, wishlistResponseDto2),
                PageRequest.of(0, 10),
                2
        );

        when(wishlistService.getAllWishlist(any(), any(int.class), any(int.class)))
                .thenReturn(wishlistResponseDtoPage);

        // When
        ApiResponse<Page<WishlistResponseDto>> response = wishlistController.getAllWishlist(userDetails, 0, 10);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");

        Page<WishlistResponseDto> page = response.getData();
        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent().get(0).getProductId()).isEqualTo(1L);
        assertThat(page.getContent().get(1).getProductId()).isEqualTo(2L);
    }
}
