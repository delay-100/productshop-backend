package com.whitedelay.productshop.cart.service;

import com.whitedelay.productshop.cart.dto.CartAllInfoResponseDto;
import com.whitedelay.productshop.cart.dto.CartInfoResponseDto;
import com.whitedelay.productshop.cart.entity.Cart;
import com.whitedelay.productshop.cart.repository.CartRepository;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import com.whitedelay.productshop.product.repository.ProductOptionRepository;
import com.whitedelay.productshop.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder().memberId("testuser").build();
    }

    @Test
    @DisplayName("장바구니 생성")
    void createCart_Success() {
        // given
        Long productId = 1L;
        Long productOptionId = 1L;
        int quantity = 1;
        Product product = Product.builder()
                .productId(productId)
                .productTitle("샘플 상품명")
                .productPrice(100)
                .build();
        ProductOption productOption = ProductOption.builder()
                .productOptionId(productOptionId)
                .productOptionTitle("샘플 상품 옵션명")
                .productOptionPrice(20)
                .build();
        Cart cart = Cart.builder()
                .member(member)
                .product(product)
                .cartProductOptionId(productOptionId)
                .cartProductQuantity(quantity)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productOptionRepository.findById(productOptionId)).thenReturn(Optional.of(productOption));
        when(cartRepository.findByMemberMemberIdAndProductProductIdAndCartProductOptionId(member.getMemberId(), productId, productOptionId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // when
        CartInfoResponseDto responseDto = cartService.createCart(member, productId, productOptionId, quantity);

        // then
        assertAll(
                () -> assertThat(responseDto.getProductId()).isEqualTo(productId),
                () -> assertThat(responseDto.getProductTitle()).isEqualTo("샘플 상품명"),
                () -> assertThat(responseDto.getProductPrice()).isEqualTo(100),
                () -> assertThat(responseDto.getQuantity()).isEqualTo(quantity),
                () -> assertThat(responseDto.getProductOptionId()).isEqualTo(productOptionId),
                () -> assertThat(responseDto.getProductOptionTitle()).isEqualTo("샘플 상품 옵션명"),
                () -> assertThat(responseDto.getProductOptionPrice()).isEqualTo(20),
                () -> assertThat(responseDto.getProductTotalPrice()).isEqualTo(120)
        );
    }

    @Test
    @DisplayName("장바구니 삭제")
    void deleteCart_Success() {
        // given
        Long productId = 1L;
        Long productOptionId = 1L;

        Cart cart = Cart.builder()
                .member(member)
                .product(new Product())
                .cartProductOptionId(productOptionId)
                .build();

        when(cartRepository.findByMemberMemberIdAndProductProductIdAndCartProductOptionId(member.getMemberId(), productId, productOptionId)).thenReturn(Optional.of(cart));

        // when
        boolean result = cartService.deleteCart(member, productId, productOptionId);

        // then
        assertThat(result).isTrue();
        verify(cartRepository).delete(cart);
    }

    @Test
    @DisplayName("장바구니 삭제 - 장바구니 내 상품 없음")
    void deleteCart_NotFound() {
        // given
        Long productId = 1L;
        Long productOptionId = 1L;

        when(cartRepository.findByMemberMemberIdAndProductProductIdAndCartProductOptionId(member.getMemberId(), productId, productOptionId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cartService.deleteCart(member, productId, productOptionId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("삭제할 상품이 없습니다.");
    }

    @Test
    @DisplayName("장바구니 전체 정보 조회")
    void getCartAllInfo_Success() {
        // given
        Product product1 = Product.builder()
                .productId(1L)
                .productTitle("반팔")
                .productPrice(41100)
                .build();
        ProductOption productOption1 = ProductOption.builder()
                .productOptionId(1L)
                .productOptionTitle("블랙/ S")
                .productOptionPrice(200)
                .build();
        Cart cart1 = Cart.builder()
                .member(member)
                .product(product1)
                .cartProductOptionId(1L)
                .cartProductQuantity(12)
                .build();

        Product product2 = Product.builder()
                .productId(2L)
                .productTitle("노트북")
                .productPrice(1500000)
                .build();
        ProductOption productOption2 = ProductOption.builder()
                .productOptionId(2L)
                .productOptionTitle("기본")
                .productOptionPrice(200)
                .build();
        Cart cart2 = Cart.builder() // 옵션이 없는 경우
                .member(member)
                .product(product2)
                .cartProductOptionId(2L)
                .cartProductQuantity(4)
                .build();

        when(cartRepository.findByMemberMemberId(member.getMemberId())).thenReturn(Arrays.asList(cart1, cart2));
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption1));
        when(productOptionRepository.findById(2L)).thenReturn(Optional.of(productOption2));

        // when
        CartAllInfoResponseDto responseDto = cartService.getCartAllInfo(member);

        // then
        assertAll(
                () -> assertThat(responseDto.getTotalPrice()).isEqualTo(6496400),
                () -> assertThat(responseDto.getCartInfoResponseDtoList()).hasSize(2),
                () -> {
                    CartInfoResponseDto cartInfo1 = responseDto.getCartInfoResponseDtoList().get(0);
                    assertAll(
                            () -> assertThat(cartInfo1.getProductId()).isEqualTo(1L),
                            () -> assertThat(cartInfo1.getProductTitle()).isEqualTo("반팔"),
                            () -> assertThat(cartInfo1.getQuantity()).isEqualTo(12),
                            () -> assertThat(cartInfo1.getProductPrice()).isEqualTo(41100),
                            () -> assertThat(cartInfo1.getProductOptionId()).isEqualTo(1L),
                            () -> assertThat(cartInfo1.getProductOptionTitle()).isEqualTo("블랙/ S"),
                            () -> assertThat(cartInfo1.getProductOptionPrice()).isEqualTo(200),
                            () -> assertThat(cartInfo1.getProductTotalPrice()).isEqualTo(495600)
                    );

                    CartInfoResponseDto cartInfo2 = responseDto.getCartInfoResponseDtoList().get(1);
                    assertAll(
                            () -> assertThat(cartInfo2.getProductId()).isEqualTo(2L),
                            () -> assertThat(cartInfo2.getProductTitle()).isEqualTo("노트북"),
                            () -> assertThat(cartInfo2.getQuantity()).isEqualTo(4),
                            () -> assertThat(cartInfo2.getProductPrice()).isEqualTo(1500000),
                            () -> assertThat(cartInfo2.getProductOptionId()).isEqualTo(2L),
                            () -> assertThat(cartInfo2.getProductOptionTitle()).isEqualTo("기본"),
                            () -> assertThat(cartInfo2.getProductOptionPrice()).isEqualTo(200),
                            () -> assertThat(cartInfo2.getProductTotalPrice()).isEqualTo(6000800)
                    );
                }
        );
    }
}
