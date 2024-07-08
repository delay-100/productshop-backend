package com.whitedelay.productshop.product.service;

import com.whitedelay.productshop.product.dto.ProductDetailResponseDto;
import com.whitedelay.productshop.product.dto.ProductOptionDetailResponseDto;
import com.whitedelay.productshop.product.dto.ProductResponseDto;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.repository.ProductOptionRepository;
import com.whitedelay.productshop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    public Page<ProductResponseDto> getAllProductList(int page, int size, String productTitle) {
        Pageable pageable = PageRequest.of(page, size);
        if (productTitle == null || productTitle.isEmpty()) {
            return productRepository.findAll(pageable).map(ProductResponseDto::from);
        }

        return productRepository.findByProductTitleContaining(productTitle, pageable).map(ProductResponseDto::from);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        List<ProductOptionDetailResponseDto> productOptions = productOptionRepository.findByProduct(product).stream()
                .map(ProductOptionDetailResponseDto::from)
                .collect(Collectors.toList());

        return ProductDetailResponseDto.from(product, productOptions);
    }
}
