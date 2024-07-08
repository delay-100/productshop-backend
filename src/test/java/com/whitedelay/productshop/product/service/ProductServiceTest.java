package com.whitedelay.productshop.product.service;

import com.whitedelay.productshop.product.dto.ProductDetailResponseDto;
import com.whitedelay.productshop.product.dto.ProductResponseDto;
import com.whitedelay.productshop.product.dto.ProductOptionDetailResponseDto;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductCategoryEnum;
import com.whitedelay.productshop.product.entity.ProductOption;
import com.whitedelay.productshop.product.entity.ProductStatusEnum;
import com.whitedelay.productshop.product.repository.ProductOptionRepository;
import com.whitedelay.productshop.product.repository.ProductRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    private Product product1;
    private Product product2;
    private ProductOption productOption1;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .productId(1L)
                .productTitle("샘플 상품1")
                .productContent("샘플 상품 내용1")
                .productStatus(ProductStatusEnum.AVAILABLE)
                .productWishlistCount(10)
                .productPrice(1000)
                .productCategory(ProductCategoryEnum.FOOD)
                .build();

        productOption1 = ProductOption.builder()
                .productOptionId(1L)
                .productOptionTitle("옵션1")
                .productOptionStock(10)
                .productOptionPrice(500)
                .productStartDate(LocalDateTime.now())
                .product(product1) // ProductOption에 Product 설정
                .build();

        product2 = Product.builder()
                .productId(2L)
                .productTitle("샘플 상품2")
                .productContent("샘플 상품 내용2")
                .productStatus(ProductStatusEnum.AVAILABLE)
                .productWishlistCount(20)
                .productPrice(20000)
                .productCategory(ProductCategoryEnum.ELECTRONICS)
                .build();
    }

    @Test
    @DisplayName("상품 리스트 전체 조회 성공")
    void getAllProductList_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product1, product2), pageable, 2);
        when(productRepository.findAll(pageable))
                .thenReturn(productPage);

        // When
        Page<ProductResponseDto> result = productService.getAllProductList(0, 10, "");

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getContent()).hasSize(2)
        );

        ProductResponseDto dto1 = result.getContent().get(0);
        assertAll(
                () -> assertThat(dto1.getProductId()).isEqualTo(1L),
                () -> assertThat(dto1.getProductTitle()).isEqualTo("샘플 상품1"),
                () -> assertThat(dto1.getProductContent()).isEqualTo("샘플 상품 내용1"),
                () -> assertThat(dto1.getProductStatus()).isEqualTo(ProductStatusEnum.AVAILABLE.getStatus()),
                () -> assertThat(dto1.getProductWishlistCount()).isEqualTo(10),
                () -> assertThat(dto1.getProductPrice()).isEqualTo(1000),
                () -> assertThat(dto1.getProductCategory()).isEqualTo(ProductCategoryEnum.FOOD.getCategory())
        );

        ProductResponseDto dto2 = result.getContent().get(1);
        assertAll(
                () -> assertThat(dto2.getProductId()).isEqualTo(2L),
                () -> assertThat(dto2.getProductTitle()).isEqualTo("샘플 상품2"),
                () -> assertThat(dto2.getProductContent()).isEqualTo("샘플 상품 내용2"),
                () -> assertThat(dto2.getProductStatus()).isEqualTo(ProductStatusEnum.AVAILABLE.getStatus()),
                () -> assertThat(dto2.getProductWishlistCount()).isEqualTo(20),
                () -> assertThat(dto2.getProductPrice()).isEqualTo(20000),
                () -> assertThat(dto2.getProductCategory()).isEqualTo(ProductCategoryEnum.ELECTRONICS.getCategory())
        );
    }

    @Test
    @DisplayName("상품명 검색 성공")
    void searchProductList_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Collections.singletonList(product2);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);
        when(productRepository.findByProductTitleContaining(eq("상품2"), any(Pageable.class)))
                .thenReturn(productPage);

        // When
        Page<ProductResponseDto> result = productService.getAllProductList(0, 10, "상품2");

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getContent()).hasSize(1)
        );

        ProductResponseDto dto = result.getContent().get(0);
        assertAll(
                () -> assertThat(dto.getProductId()).isEqualTo(2L),
                () -> assertThat(dto.getProductTitle()).isEqualTo("샘플 상품2"),
                () -> assertThat(dto.getProductContent()).isEqualTo("샘플 상품 내용2"),
                () -> assertThat(dto.getProductStatus()).isEqualTo(ProductStatusEnum.AVAILABLE.getStatus()),
                () -> assertThat(dto.getProductWishlistCount()).isEqualTo(20),
                () -> assertThat(dto.getProductPrice()).isEqualTo(20000),
                () -> assertThat(dto.getProductCategory()).isEqualTo(ProductCategoryEnum.ELECTRONICS.getCategory())
        );
    }

    @Test
    @DisplayName("상품 상세 정보 조회 성공")
    void getProductDetail_Success() {
        // Given
        when(productRepository.findByProductId(any(Long.class)))
                .thenReturn(Optional.of(product1));
        when(productOptionRepository.findByProduct(product1))
                .thenReturn(Collections.singletonList(productOption1));

        // When
        ProductDetailResponseDto result = productService.getProductDetail(1L);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getProductId()).isEqualTo(1L),
                () -> assertThat(result.getProductTitle()).isEqualTo("샘플 상품1"),
                () -> assertThat(result.getProductContent()).isEqualTo("샘플 상품 내용1"),
                () -> assertThat(result.getProductStatus()).isEqualTo(ProductStatusEnum.AVAILABLE.getStatus()),
                () -> assertThat(result.getProductWishlistCount()).isEqualTo(10),
                () -> assertThat(result.getProductPrice()).isEqualTo(1000),
                () -> assertThat(result.getProductCategory()).isEqualTo(ProductCategoryEnum.FOOD.getCategory()),
                () -> assertThat(result.getProductOptions()).hasSize(1)
        );

        ProductOptionDetailResponseDto optionDto = result.getProductOptions().get(0);
        assertAll(
                () -> assertThat(optionDto.getProductOptionId()).isEqualTo(1L),
                () -> assertThat(optionDto.getProductOptionTitle()).isEqualTo("옵션1"),
                () -> assertThat(optionDto.getProductOptionStock()).isEqualTo(10),
                () -> assertThat(optionDto.getProductOptionPrice()).isEqualTo(500),
                () -> assertThat(optionDto.getProductStartDate()).isNotNull()
        );
    }

    @Test
    @DisplayName("상품 상세 정보 조회 실패")
    void getProductDetail_NotFound() {
        // Given
        when(productRepository.findByProductId(any(Long.class)))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productService.getProductDetail(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("상품을 찾을 수 없습니다.");
    }
}
