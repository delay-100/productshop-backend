package com.whitedelay.productshop.wishlist.service;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.repository.ProductRepository;
import com.whitedelay.productshop.security.jwt.JwtUtil;
import com.whitedelay.productshop.wishlist.dto.WishlistRequestDto;
import com.whitedelay.productshop.wishlist.dto.WishlistResponseDto;
import com.whitedelay.productshop.wishlist.entity.Wishlist;
import com.whitedelay.productshop.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    @Transactional
    public boolean createWishlistWish(Member member, Long productId) {
        try {
            // 상품 조회
            Product product = productRepository.findByProductId(productId)
                    .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));

            // 위시리스트에 상품이 이미 존재하는지 확인
            boolean exists = wishlistRepository.existsByMemberMemberIdAndProductProductId(member.getMemberId(), productId);
            if (exists) {
                throw new IllegalArgumentException("이미 등록되어있는 상품입니다.");
            }

            // Wishlist 생성 및 저장
            Wishlist wishlist = Wishlist.from(WishlistRequestDto.builder()
                    .member(member)
                    .product(product)
                    .build());
            wishlistRepository.save(wishlist);

            // product의 wishlistCount 증가
            product.setProductWishlistCount(product.getProductWishlistCount() + 1);
            productRepository.save(product); // product 객체를 업데이트

            return true;

        } catch (Exception e) {
            log.error("위시리스트 생성 중 오류 발생: ", e);
            throw e;
        }
    }


    @Transactional
    public boolean deleteWishlistWish(Member member, Long productId) {
        try {
            // 상품 조회
            Product product = productRepository.findByProductId(productId)
                    .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));

            // wishlist조회 했는데 이미 값이 있는 경우 삭제
            Optional<Wishlist> wishlist = wishlistRepository.findByMemberMemberIdAndProductProductId(member.getMemberId(), productId);
            if (wishlist.isPresent()) {
                wishlistRepository.delete(wishlist.get());
            }
            return true;

        } catch (Exception e) {
            log.error("위시리스트 삭제 중 오류 발생: ", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Page<WishlistResponseDto> getAllWishlist(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return wishlistRepository.findByMemberMemberId(member.getMemberId(), pageable).map(WishlistResponseDto::from);
    }
}
