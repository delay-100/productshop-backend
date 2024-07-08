package com.whitedelay.productshop.order.repository;

import com.whitedelay.productshop.order.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query("SELECT op FROM OrderProduct op WHERE op.order.orderId = :orderId")
    List<OrderProduct> findByOrderOrderId(@Param("orderId") Long orderId);

}
