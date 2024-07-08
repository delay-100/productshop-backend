package com.whitedelay.productshop.product.controller;

import com.whitedelay.productshop.product.dto.ProductDetailResponseDto;
import com.whitedelay.productshop.product.dto.ProductResponseDto;
import com.whitedelay.productshop.product.entity.ProductCategoryEnum;
import com.whitedelay.productshop.product.entity.ProductStatusEnum;
import com.whitedelay.productshop.product.service.ProductService;
import com.whitedelay.productshop.util.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductResponseDto productResponseDto1;
    private ProductResponseDto productResponseDto2;
    private ProductDetailResponseDto productDetailResponseDto;

    @BeforeEach
    void setUp() {
        productResponseDto1 = ProductResponseDto.builder()
                .productId(1L)
                .productTitle("샘플 상품1")
                .productContent("샘플 상품 내용1")
                .productStatus(ProductStatusEnum.AVAILABLE.getStatus())
                .productWishlistCount(10)
                .productPrice(1000)
                .productCategory(ProductCategoryEnum.FOOD.getCategory())
                .build();

        productResponseDto2 = ProductResponseDto.builder()
                .productId(2L)
                .productTitle("샘플 상품2")
                .productContent("샘플 상품 내용2")
                .productStatus(ProductStatusEnum.AVAILABLE.getStatus())
                .productWishlistCount(20)
                .productPrice(20000)
                .productCategory(ProductCategoryEnum.ELECTRONICS.getCategory())
                .build();

        productDetailResponseDto = ProductDetailResponseDto.builder()
                .productId(1L)
                .productTitle("샘플 상품 상세1")
                .productContent("샘플 상품 상세 내용1")
                .productStatus(ProductStatusEnum.AVAILABLE.getStatus())
                .productWishlistCount(10)
                .productPrice(1000)
                .productCategory(ProductCategoryEnum.FOOD.getCategory())
                .productOptions(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("상품 리스트 전체 조회 성공")
    void getAllProductList_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductResponseDto> productList = Arrays.asList(productResponseDto1, productResponseDto2);
        Page<ProductResponseDto> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productService.getAllProductList(anyInt(), anyInt(), eq(""))).thenReturn(productPage);

        // When
        ApiResponse<Page<ProductResponseDto>> response = productController.getAllProductList(0, 10, "");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getContent()).hasSize(2);

        ProductResponseDto dto1 = response.getData().getContent().get(0);
        assertThat(dto1.getProductId()).isEqualTo(1L);
        assertThat(dto1.getProductTitle()).isEqualTo("샘플 상품1");
        assertThat(dto1.getProductContent()).isEqualTo("샘플 상품 내용1");
        assertThat(dto1.getProductStatus()).isEqualTo(ProductStatusEnum.AVAILABLE.getStatus());
        assertThat(dto1.getProductWishlistCount()).isEqualTo(10);
        assertThat(dto1.getProductPrice()).isEqualTo(1000);
        assertThat(dto1.getProductCategory()).isEqualTo(ProductCategoryEnum.FOOD.getCategory());

        ProductResponseDto dto2 = response.getData().getContent().get(1);
        assertThat(dto2.getProductId()).isEqualTo(2L);
        assertThat(dto2.getProductTitle()).isEqualTo("샘플 상품2");
        assertThat(dto2.getProductContent()).isEqualTo("샘플 상품 내용2");
        assertThat(dto2.getProductStatus()).isEqualTo(ProductStatusEnum.AVAILABLE.getStatus());
        assertThat(dto2.getProductWishlistCount()).isEqualTo(20);
        assertThat(dto2.getProductPrice()).isEqualTo(20000);
        assertThat(dto2.getProductCategory()).isEqualTo(ProductCategoryEnum.ELECTRONICS.getCategory());
    }

    @Test
    @DisplayName("상품명 검색 성공")
    void searchProductList_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductResponseDto> productList = Arrays.asList(productResponseDto1, productResponseDto2);
        Page<ProductResponseDto> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productService.getAllProductList(anyInt(), anyInt(), eq("샘플"))).thenReturn(productPage);

        // When
        ApiResponse<Page<ProductResponseDto>> response = productController.getAllProductList(0, 10, "샘플");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getContent()).hasSize(2);

        ProductResponseDto dto1 = response.getData().getContent().get(0);
        assertThat(dto1.getProductId()).isEqualTo(1L);
        assertThat(dto1.getProductTitle()).isEqualTo("샘플 상품1");
        assertThat(dto1.getProductContent()).isEqualTo("샘플 상품 내용1");
        assertThat(dto1.getProductStatus()).isEqualTo(ProductStatusEnum.AVAILABLE.getStatus());
        assertThat(dto1.getProductWishlistCount()).isEqualTo(10);
        assertThat(dto1.getProductPrice()).isEqualTo(1000);
        assertThat(dto1.getProductCategory()).isEqualTo(ProductCategoryEnum.FOOD.getCategory());

        ProductResponseDto dto2 = response.getData().getContent().get(1);
        assertThat(dto2.getProductId()).isEqualTo(2L);
        assertThat(dto2.getProductTitle()).isEqualTo("샘플 상품2");
        assertThat(dto2.getProductContent()).isEqualTo("샘플 상품 내용2");
        assertThat(dto2.getProductStatus()).isEqualTo(ProductStatusEnum.AVAILABLE.getStatus());
        assertThat(dto2.getProductWishlistCount()).isEqualTo(20);
        assertThat(dto2.getProductPrice()).isEqualTo(20000);
        assertThat(dto2.getProductCategory()).isEqualTo(ProductCategoryEnum.ELECTRONICS.getCategory());
    }

    @Test
    @DisplayName("상품명 검색 성공2")
    void searchProductList_Success2() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductResponseDto> productList = Collections.singletonList(productResponseDto2);
        Page<ProductResponseDto> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productService.getAllProductList(anyInt(), anyInt(), eq("상품2"))).thenReturn(productPage);

        // When
        ApiResponse<Page<ProductResponseDto>> response = productController.getAllProductList(0, 10, "상품2");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getContent()).hasSize(1);

        ProductResponseDto dto2 = response.getData().getContent().get(0); // index 수정
        assertThat(dto2.getProductId()).isEqualTo(2L);
        assertThat(dto2.getProductTitle()).isEqualTo("샘플 상품2");
        assertThat(dto2.getProductContent()).isEqualTo("샘플 상품 내용2");
        assertThat(dto2.getProductStatus()).isEqualTo(ProductStatusEnum.AVAILABLE.getStatus());
        assertThat(dto2.getProductWishlistCount()).isEqualTo(20);
        assertThat(dto2.getProductPrice()).isEqualTo(20000);
        assertThat(dto2.getProductCategory()).isEqualTo(ProductCategoryEnum.ELECTRONICS.getCategory());
    }

    @Test
    @DisplayName("상품 상세 정보 조회 성공")
    void getProductDetail_Success() {
        // Given
        when(productService.getProductDetail(any(Long.class))).thenReturn(productDetailResponseDto);

        // When
        ApiResponse<ProductDetailResponseDto> response = productController.getProductDetail(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");

        ProductDetailResponseDto dto = response.getData();
        assertThat(dto.getProductId()).isEqualTo(1L);
        assertThat(dto.getProductTitle()).isEqualTo("샘플 상품 상세1");
        assertThat(dto.getProductContent()).isEqualTo("샘플 상품 상세 내용1");
        assertThat(dto.getProductStatus()).isEqualTo(ProductStatusEnum.AVAILABLE.getStatus());
        assertThat(dto.getProductWishlistCount()).isEqualTo(10);
        assertThat(dto.getProductPrice()).isEqualTo(1000);
        assertThat(dto.getProductCategory()).isEqualTo(ProductCategoryEnum.FOOD.getCategory());
        assertThat(dto.getProductOptions()).isEmpty();
    }
}
