package com.whitedelay.productshop.product.repository;

import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
//    Optional<ProductOption> findByProductOptionId(Long orderProductOptionId);

    List<ProductOption> findByProduct(Product product);

    Optional<ProductOption> findByProductOptionId(Long productOptionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductOption p WHERE p.productOptionId = :productOptionId")
    Optional<ProductOption> findByIdForUpdate(@Param("productOptionId") Long productOptionId);

    @Modifying
    @Query("UPDATE ProductOption po SET po.productOptionStock = :quantity WHERE po.productOptionId = :productOptionId AND po.productOptionStock >= :quantity")
    int updateStock(@Param("productOptionId") Long productOptionId, @Param("quantity") int quantity);

//
//    return jpaQueryFactory.select(
//            new QMemberDto(
//            member.userId,
//            member.password))
//            .from(member)
//            .where(member.userId.eq(userId))
//            .fetchFirst();
//
//    return jpaQueryFactory.select(
//        member.userId,
//        member.password)
//            .from(member)
//            .where(member.userId.eq(userId))
//            .fetchFirst(); // 완성시키고 보냄, 하나만가져옴
}
