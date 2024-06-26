package com.whitedelay.productshop.order.repository;

import com.whitedelay.productshop.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
