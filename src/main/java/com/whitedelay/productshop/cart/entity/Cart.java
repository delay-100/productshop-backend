package com.whitedelay.productshop.cart.entity;

import com.whitedelay.productshop.cart.dto.CartRequestDto;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    private Long cartProductOptionId; // 상품 옵션이 없을 수도 있음

    @Column(nullable = false)
    private int cartProductQuantity; // 카트에 담은 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static Cart from(CartRequestDto cart) {
        return Cart.builder()
                .cartProductOptionId(cart.getCartProductOptionId())
                .cartProductQuantity(cart.getCartProductQuantity())
                .member(cart.getMember())
                .product(cart.getProduct())
                .build();
    }

    public void setCartProductQuantity(int cartProductStock) {
        this.cartProductQuantity = cartProductStock;
    }
}
