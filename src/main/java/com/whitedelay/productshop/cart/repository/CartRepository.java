package com.whitedelay.productshop.cart.repository;

import com.whitedelay.productshop.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>  {

    Optional<Cart> findByMemberMemberIdAndProductProductIdAndCartProductOptionId(String memberId, long productId, long cartProductOptionId);
    List<Cart> findByMemberMemberId(String memberId);
}
