package com.whitedelay.productshop.image.dto;

import com.whitedelay.productshop.image.entity.ImageTableEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ImageRequestDto {
    private String imageUrl;
    private ImageTableEnum imageTable;
    private Long imageTableId;
    private String imageFileName;

    public static ImageRequestDto from (String imageUrl, ImageTableEnum imageTable, Long imageTableId, String imageFileName) {
        return ImageRequestDto.builder()
                .imageUrl(imageUrl)
                .imageTable(imageTable)
                .imageTableId(imageTableId)
                .imageFileName(imageFileName)
                .build();
    }
}
