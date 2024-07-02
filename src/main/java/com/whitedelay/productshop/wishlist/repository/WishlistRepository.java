package com.whitedelay.productshop.wishlist.repository;

import com.whitedelay.productshop.wishlist.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByMemberMemberIdAndProductProductId(String memberId, Long productId);

    Page<Wishlist> findByMemberMemberId(String memberId, Pageable pageable);

    boolean existsByMemberMemberIdAndProductProductId(String memberId, Long productId);
}
