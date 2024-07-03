package com.whitedelay.productshop.cart.controller;

import com.whitedelay.productshop.cart.dto.CartAllInfoResponseDto;
import com.whitedelay.productshop.cart.dto.CartInfoRequestDto;
import com.whitedelay.productshop.cart.dto.CartInfoResponseDto;
import com.whitedelay.productshop.cart.dto.CartSimpleInfoRequestDto;
import com.whitedelay.productshop.cart.service.CartService;
import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartService cartService;

    @Mock
    private UserDetailsImpl userDetails;

    private CartInfoRequestDto cartInfoRequestDto;
    private CartInfoResponseDto cartInfoResponseDto;
    private CartSimpleInfoRequestDto cartSimpleInfoRequestDto;
    private CartAllInfoResponseDto cartAllInfoResponseDto;

    @BeforeEach
    void setUp() {
        cartInfoRequestDto = CartInfoRequestDto.builder()
                .productId(1L)
                .productOptionId(1L)
                .quantity(5)
                .build();

        cartInfoResponseDto = CartInfoResponseDto.builder()
                .productId(1L)
                .productTitle("샘플 상품1")
                .quantity(5)
                .productPrice(200) // 상품의 기본 가격
                .productOptionId(1L)
                .productOptionTitle("옵션1")
                .productOptionPrice(500) // 옵션의 추가 가격
                .productTotalPrice(5 * (200 + 500)) // 5 * (기본 가격 + 옵션 가격)
                .build();

        cartSimpleInfoRequestDto = CartSimpleInfoRequestDto.builder()
                .productId(1L)
                .productOptionId(1L)
                .build();

        cartAllInfoResponseDto = CartAllInfoResponseDto.builder()
                .cartInfoResponseDtoList(Collections.singletonList(cartInfoResponseDto))
                .totalPrice(5 * (200 + 500)) // 5 * (기본 가격 + 옵션 가격)
                .build();

        when(userDetails.getMember()).thenReturn(null); // 테스트용 사용자 정보 설정
    }

    @Test
    @DisplayName("장바구니 추가 성공")
    void createCart_Success() {
        // Given
        when(cartService.createCart(any(), any(Long.class), any(Long.class), any(int.class)))
                .thenReturn(cartInfoResponseDto);

        // When
        ApiResponse<CartInfoResponseDto> response = cartController.createCart(userDetails, cartInfoRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");

        CartInfoResponseDto dto = response.getData();
        assertThat(dto.getProductId()).isEqualTo(1L);
        assertThat(dto.getProductTitle()).isEqualTo("샘플 상품1");
        assertThat(dto.getQuantity()).isEqualTo(5);
        assertThat(dto.getProductPrice()).isEqualTo(200);
        assertThat(dto.getProductOptionId()).isEqualTo(1L);
        assertThat(dto.getProductOptionTitle()).isEqualTo("옵션1");
        assertThat(dto.getProductOptionPrice()).isEqualTo(500);
        assertThat(dto.getProductTotalPrice()).isEqualTo(5 * (200 + 500)); // 5 * (기본 가격 + 옵션 가격)
    }

    @Test
    @DisplayName("장바구니 삭제 성공")
    void deleteCart_Success() {
        // Given
        when(cartService.deleteCart(any(), any(Long.class), any(Long.class)))
                .thenReturn(true);

        // When
        ApiResponse<Boolean> response = cartController.deleteCart(userDetails, cartSimpleInfoRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    @DisplayName("장바구니 전체 조회 성공")
    void getCartAllInfo_Success() {
        // Given
        when(cartService.getCartAllInfo(any()))
                .thenReturn(cartAllInfoResponseDto);

        // When
        ApiResponse<CartAllInfoResponseDto> response = cartController.getCartAllInfo(userDetails);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");

        CartAllInfoResponseDto dto = response.getData();
        assertThat(dto.getCartInfoResponseDtoList()).hasSize(1);
        assertThat(dto.getTotalPrice()).isEqualTo(5 * (200 + 500)); // 5 * (기본 가격 + 옵션 가격)
    }
}
