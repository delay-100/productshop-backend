package com.whitedelay.productshop.order.repository;

import com.whitedelay.productshop.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // JPQL 사용
//    @Query("SELECT o FROM Order o WHERE o.member.memberId = :memberId")
//    Page<Order> findByMemberId(@Param("memberId") String memberId, Pageable pageable);
//
//    @Query("SELECT o FROM Order o WHERE o.member.memberId = :memberId AND o.orderId = :orderId")
//    Order findByMemberIdAndOrderId(@Param("memberId") String memberId, @Param("orderId") Long orderId);

    // SPRING DATA JPA 사용
    Page<Order> findByMemberMemberId(String memberId, Pageable pageable);

    Order findByMemberMemberIdAndOrderId(String memberId, Long orderId);
}
