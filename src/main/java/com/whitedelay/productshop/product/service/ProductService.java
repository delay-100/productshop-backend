package com.whitedelay.productshop.product.service;

import com.whitedelay.productshop.image.dto.ImageInfoRequestDto;
import com.whitedelay.productshop.image.dto.ImageRequestDto;
import com.whitedelay.productshop.image.dto.ImageResponseDto;
import com.whitedelay.productshop.image.entity.Image;
import com.whitedelay.productshop.image.entity.ImageTableEnum;
import com.whitedelay.productshop.image.service.ImageService;
import com.whitedelay.productshop.product.dto.*;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import com.whitedelay.productshop.product.repository.ProductOptionRepository;
import com.whitedelay.productshop.product.repository.ProductRepository;
import com.whitedelay.productshop.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ImageService imageService;
    private final RedisService redisService;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    public Page<ProductListResponseDto> getAllProductList(int page, int size, String productTitle) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        if (productTitle == null || productTitle.isEmpty()) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.findByProductTitleContaining(productTitle, pageable);
        }

        return products.map(product -> {
            ImageResponseDto imageResponse = imageService.findImageResponse(ImageTableEnum.PRODUCT, product.getProductId());
            return ProductListResponseDto.from(product, imageResponse);
        });
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        List<ProductOptionDetailResponseDto> productOptionList = productOptionRepository.findByProduct(product).stream()
                .map(ProductOptionDetailResponseDto::from)
                .collect(Collectors.toList());

        List<ImageResponseDto> imageResponseDtoList = imageService.findImageResponseList(ImageTableEnum.PRODUCT, productId);

        return ProductDetailResponseDto.from(product, productOptionList, imageResponseDtoList);
    }

    public ProductResponseDto createProduct(ProductRequestDto productRequestDto, List<MultipartFile> imageFileList, List<ProductOptionRequestDto> productOptionRequestDtoList) {
        // 리스트가 비어 있는지 확인
        if (imageFileList == null || imageFileList.isEmpty()) {
            throw new IllegalArgumentException("이미지가 없습니다.");
        }

        for (MultipartFile file : imageFileList) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("빈 파일이 포함되어 있습니다.");
            }
        }
        if (productOptionRequestDtoList == null || productOptionRequestDtoList.isEmpty()) {
            throw new IllegalArgumentException("상품 옵션이 없습니다.(상품 옵션은 기본1개)");
        }

        Product product = Product.from(productRequestDto);
        productRepository.save(product);

        List<ImageResponseDto> imageResponseDto = imageService.uploadMultiImage(
                ImageInfoRequestDto.from(ImageTableEnum.PRODUCT, product.getProductId()),
                imageFileList
        );

        // 상품 옵션 추가
        createProductOption(product.getProductId(), productOptionRequestDtoList);

        return ProductResponseDto.from(product, imageResponseDto);
    }

    public List<ProductOptionResponseDto> createProductOption(Long productId, List<ProductOptionRequestDto> productOptionRequestDtoList) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다."));

        if (productOptionRequestDtoList == null || productOptionRequestDtoList.isEmpty()) {
            throw new IllegalArgumentException("추가할 상품 옵션이 없습니다.(상품 옵션은 기본1개)");
        }

        List<ProductOption> productOptionList = productOptionRequestDtoList.stream()
                .map(optionDto -> ProductOption.from(optionDto, product))
                .collect(Collectors.toList());
        productOptionRepository.saveAll(productOptionList);

        saveProductOptionStockToRedis(productOptionList);

        return ProductOptionResponseDto.from(productOptionList);
    }

    private void saveProductOptionStockToRedis(List<ProductOption> optionList) {
        for (ProductOption option : optionList) {
            redisService.setInitialStock(option.getProductOptionId(), option.getProductOptionStock());
        }
    }
}
