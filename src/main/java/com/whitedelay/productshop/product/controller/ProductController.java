package com.whitedelay.productshop.product.controller;

import com.whitedelay.productshop.product.dto.ProductDetailResponseDto;
import com.whitedelay.productshop.product.dto.ProductResponseDto;
import com.whitedelay.productshop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public Page<ProductResponseDto> getAllProductList(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String productTitle) {
        return productService.getAllProductList(page, size, productTitle);
    }

    @GetMapping("/products/{productId}")
    public ProductDetailResponseDto getProductDetail(@PathVariable Long productId) {
        return productService.getProductDetail(productId);
    }
}
