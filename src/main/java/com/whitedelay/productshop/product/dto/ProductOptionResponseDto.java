package com.whitedelay.productshop.product.dto;

import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOptionResponseDto {
    private Long productOptionId;
    private String productOptionTitle;
    private int productOptionPrice;

    public static ProductOptionResponseDto from(ProductOption productOption) {
        return ProductOptionResponseDto.builder()
                .productOptionId(productOption.getProductOptionId())
                .productOptionTitle(productOption.getProductOptionTitle())
                .productOptionPrice(productOption.getProductOptionPrice())
                .build();
    }

    public static List<ProductOptionResponseDto> from(List<ProductOption> productOptionList) {
        return productOptionList.stream()
                .map(ProductOptionResponseDto::from)
                .collect(Collectors.toList());
    }
}
