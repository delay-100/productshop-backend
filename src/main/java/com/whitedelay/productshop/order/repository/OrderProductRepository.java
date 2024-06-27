package com.whitedelay.productshop.order.repository;

import com.whitedelay.productshop.order.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
