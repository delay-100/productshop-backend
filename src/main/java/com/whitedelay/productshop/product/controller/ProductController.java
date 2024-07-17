package com.whitedelay.productshop.product.controller;

import com.whitedelay.productshop.product.dto.*;
import com.whitedelay.productshop.product.service.ProductService;
import com.whitedelay.productshop.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ApiResponse<Page<ProductListResponseDto>> getAllProductList(
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

    /**
     * POST
     * 상품 및 상품 옵션 추가
     * @param productWithOptionsRequestDto 추가할 상품, 상품 옵션 정보
     * @return 추가된 상품 정보 DTO
     */
    @PostMapping(BASE_PRODUCT) // 권한 처리 추가
    public ApiResponse<ProductResponseDto> createProduct(
            @RequestPart ProductWithOptionsRequestDto productWithOptionsRequestDto,
            @RequestPart List<MultipartFile> imageFileList
            ) {
        return ApiResponse.createSuccess(productService.createProduct(productWithOptionsRequestDto.getProductRequestDto(), imageFileList, productWithOptionsRequestDto.getProductOptionRequestDto()));
    }

    /**
     * POST
     * 상품 옵션 추가
     * @param productId 옵션을 추가할 상품 아이디
     * @param productOptionRequestDtoList 추가할 옵션 리스트
     * @return 추가된 상품
     */
    @PostMapping(BASE_PRODUCT + "/{productId}")
    public ApiResponse<List<ProductOptionResponseDto>> createProductOption(
            @PathVariable Long productId,
            @RequestBody List<ProductOptionRequestDto> productOptionRequestDtoList
    ) {
        return ApiResponse.createSuccess(productService.createProductOption(productId, productOptionRequestDtoList));
    }


    /**
     * POST
     * 옵션별 상품 재고 추가
     * @param productId 상품 아이디
     * @param productOptionId 상품 옵션 아이디
     * @param productOptionStockRequestDto 재고
     * @return 상품 옵션 재고 추가 여부(T/F)
     */
    @PostMapping(BASE_PRODUCT + "/{productId}/{productOptionId}")
    public ApiResponse<Boolean> updateProductOptionStock(
        @PathVariable Long productId,
        @PathVariable Long productOptionId,
        @RequestBody ProductOptionStockRequestDto productOptionStockRequestDto
    ) {
        return ApiResponse.createSuccess(productService.updateProductOptionStock(productId, productOptionId, productOptionStockRequestDto));
    }
}
