package com.whitedelay.productshop.product.controller;

import com.whitedelay.productshop.product.dto.ProductDetailResponseDto;
import com.whitedelay.productshop.product.dto.ProductResponseDto;
import com.whitedelay.productshop.product.service.ProductService;
import com.whitedelay.productshop.util.ApiResponse;
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

    private static final String BASE_PRODUCT = "/products";

    /**
     * GET
     * 상품 정보 리스트 및 상품명 검색
     * @param page 페이지 번호
     * @param size 한 페이지에 띄울 수
     * @param productTitle 상품명
     * @return 상품 정보 리스트 DTO
     */
    @GetMapping(BASE_PRODUCT)
    public ApiResponse<Page<ProductResponseDto>> getAllProductList(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String productTitle
    ) {
        return ApiResponse.createSuccess(productService.getAllProductList(page, size, productTitle));
    }

    /**
     * GET
     * 상품 정보 상세
     * @param productId 상품 아이디
     * @return 상품 정보 상세 DTO
     */
    @GetMapping(BASE_PRODUCT + "/{productId}")
    public ApiResponse<ProductDetailResponseDto> getProductDetail(
            @PathVariable Long productId
    ) {
        return ApiResponse.createSuccess(productService.getProductDetail(productId));
    }
}
