package com.whitedelay.productshop.product.repository;

import com.whitedelay.productshop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
        Page<Product> findByProductTitleContaining(String productTitle, Pageable pageable);
}
