package com.whitedelay.productshop.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ImageResponseDto {
    String uploadImageUrl;
    String fileName;

    public static ImageResponseDto from(String uploadImageUrl, String fileName) {
        return ImageResponseDto.builder()
                .uploadImageUrl(uploadImageUrl)
                .fileName(fileName)
                .build();
    }
}
