package com.whitedelay.productshop.order.repository;

import com.whitedelay.productshop.order.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByOrderOrderId(Long orderId);
}
