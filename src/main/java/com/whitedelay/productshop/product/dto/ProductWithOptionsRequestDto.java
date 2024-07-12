package com.whitedelay.productshop.product.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ProductWithOptionsRequestDto {
    ProductRequestDto productRequestDto;
    List<ProductOptionRequestDto> productOptionRequestDto;
}
