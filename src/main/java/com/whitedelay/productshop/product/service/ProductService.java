package com.whitedelay.productshop.product.service;

import com.whitedelay.productshop.product.dto.ProductDetailResponseDto;
import com.whitedelay.productshop.product.dto.ProductResponseDto;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.repository.ProductOptionRepository;
import com.whitedelay.productshop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    public Page<ProductResponseDto> getAllProductList(int page, int size, String productTitle) {
        Pageable pageable = PageRequest.of(page, size);
        if (productTitle == null || productTitle.isEmpty()) {
            return productRepository.findAll(pageable).map(ProductResponseDto::from);
        } else {
            return productRepository.findByProductTitleContaining(productTitle, pageable).map(ProductResponseDto::from);
        }
    }

    public ProductDetailResponseDto getProductDetail(Long productId) {
        Optional<Product> product = productRepository.findByProductId(productId);
        return product.map(ProductDetailResponseDto::from).orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
