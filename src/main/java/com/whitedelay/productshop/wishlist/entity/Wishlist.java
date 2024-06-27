package com.whitedelay.productshop.wishlist.entity;
//
//import com.whitedelay.productshop.member.entity.Member;
//import com.whitedelay.productshop.product.entity.Product;
//import com.whitedelay.productshop.wishlist.dto.WishlistRequestDto;
//import jakarta.persistence.*;
//import lombok.*;
//
//@Builder(access = AccessLevel.PRIVATE)
//@Getter
//@Entity
//@AllArgsConstructor(access = AccessLevel.PRIVATE)
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(name="wishlist")
//public class Wishlist extends Timestamped {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long wishlistId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;
//
////    public static Wishlist from(WishlistRequestDto wishlist) {
////        return Wishlist.builder()
////                .member(wishlist.getMember())
////                .product(wishlist.getProduct())
////                .build();
////    }
//}

import com.whitedelay.productshop.member.dto.MemberRequestDto;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.wishlist.dto.WishlistRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlist")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Wishlist extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    public static Wishlist from(WishlistRequestDto wishlist) {
        return Wishlist.builder()
                .product(wishlist.getProduct())
                .member(wishlist.getMember())
                .build();
    }
}
