package com.whitedelay.productshop.product.service;

import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductStatusEnum;
import com.whitedelay.productshop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStatusUpdateService {

    private final ProductRepository productRepository;

    @Scheduled(cron ="0 * * * * *")
    @Transactional
    public void updateProductStatus() {
        List<Product> products = productRepository.findByProductStatusAndProductStartDateBefore(ProductStatusEnum.COMING_SOON, LocalDateTime.now());
        for(Product product : products) {
            product.setProductStatus(ProductStatusEnum.AVAILABLE);
            productRepository.save(product);
        }
    }

}
