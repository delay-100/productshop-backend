package com.whitedelay.productshop.wishlist.service;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.repository.ProductRepository;
import com.whitedelay.productshop.wishlist.dto.WishlistResponseDto;
import com.whitedelay.productshop.wishlist.entity.Wishlist;
import com.whitedelay.productshop.wishlist.repository.WishlistRepository;
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
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @InjectMocks
    private WishlistService wishlistService;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductRepository productRepository;

    private Member member;
    private Product product1;
    private Product product2;
    private Wishlist wishlist1;
    private Wishlist wishlist2;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .memberId("testuser")
                .build();

        product1 = Product.builder()
                .productId(1L)
                .productTitle("Sample Product 1")
                .productWishlistCount(0)
                .build();

        product2 = Product.builder()
                .productId(2L)
                .productTitle("Sample Product 2")
                .productWishlistCount(0)
                .build();

        wishlist1 = Wishlist.builder()
                .member(member)
                .product(product1)
                .build();

        wishlist2 = Wishlist.builder()
                .member(member)
                .product(product2)
                .build();
    }

    @Test
    @DisplayName("위시리스트 상품 추가")
    void createWishlistWish_Success() {
        // given
        when(productRepository.findByProductId(product1.getProductId())).thenReturn(Optional.of(product1));
        when(wishlistRepository.existsByMemberMemberIdAndProductProductId(member.getMemberId(), product1.getProductId())).thenReturn(false);

        // when
        boolean result = wishlistService.createWishlistWish(member, product1.getProductId());

        // then
        assertAll(
                () -> assertThat(result).isTrue(),
                () -> assertThat(product1.getProductWishlistCount()).isEqualTo(1),
                () -> verify(wishlistRepository).save(any(Wishlist.class)),
                () -> verify(productRepository).save(product1)
        );
    }

    @Test
    @DisplayName("위시리스트 상품 추가 실패 - 상품이 이미 존재")
    void createWishlistWish_ProductAlreadyExists() {
        // given
        when(productRepository.findByProductId(product1.getProductId())).thenReturn(Optional.of(product1));
        when(wishlistRepository.existsByMemberMemberIdAndProductProductId(member.getMemberId(), product1.getProductId())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> wishlistService.createWishlistWish(member, product1.getProductId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 등록되어있는 상품입니다.");
    }

    @Test
    @DisplayName("위시리스트 상품 삭제")
    void deleteWishlistWish_Success() {
        // given
        when(productRepository.findByProductId(product1.getProductId())).thenReturn(Optional.of(product1));
        when(wishlistRepository.findByMemberMemberIdAndProductProductId(member.getMemberId(), product1.getProductId())).thenReturn(Optional.of(wishlist1));

        // when
        boolean result = wishlistService.deleteWishlistWish(member, product1.getProductId());

        // then
        assertAll(
                () -> assertThat(result).isTrue(),
                () -> verify(wishlistRepository).delete(wishlist1)
        );
    }

    @Test
    @DisplayName("위시리스트 상품 삭제 실패 - 상품이 존재하지 않음")
    void deleteWishlistWish_ProductNotFound() {
        // given
        when(productRepository.findByProductId(product1.getProductId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> wishlistService.deleteWishlistWish(member, product1.getProductId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("찾는 상품이 없습니다.");
    }

    @Test
    @DisplayName("위시리스트 조회")
    void getAllWishlist_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Wishlist> wishlistPage = new PageImpl<>(List.of(wishlist1, wishlist2), pageable, 2);
        when(wishlistRepository.findByMemberMemberId(member.getMemberId(), pageable)).thenReturn(wishlistPage);

        // when
        Page<WishlistResponseDto> result = wishlistService.getAllWishlist(member, 0, 10);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getContent()).hasSize(2)
        );

        WishlistResponseDto dto1 = result.getContent().get(0);
        assertAll(
                () -> assertThat(dto1.getProductId()).isEqualTo(product1.getProductId()),
                () -> assertThat(dto1.getProductTitle()).isEqualTo(product1.getProductTitle())
        );

        WishlistResponseDto dto2 = result.getContent().get(1);
        assertAll(
                () -> assertThat(dto2.getProductId()).isEqualTo(product2.getProductId()),
                () -> assertThat(dto2.getProductTitle()).isEqualTo(product2.getProductTitle())
        );
    }
}
