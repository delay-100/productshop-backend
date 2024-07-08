package com.whitedelay.productshop.product.repository;

import com.whitedelay.productshop.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
        Page<Product> findByProductTitleContaining(String productTitle, Pageable pageable);
        Optional<Product> findByProductId(Long productId);

//        @Lock(LockModeType.PESSIMISTIC_WRITE)
//        @Query("SELECT p FROM Product p WHERE p.productId = :productId")
//        Optional<Product> findByIdForUpdate(@Param("productId") Long productId);

//        @Modifying
//        @Query("UPDATE Product p SET p.productStock = :quantity WHERE p.productId = :productId")
//        int updateStock(@Param("productId") Long productId, @Param("quantity") int quantity);
}
