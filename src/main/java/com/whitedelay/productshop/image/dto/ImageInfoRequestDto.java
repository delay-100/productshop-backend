package com.whitedelay.productshop.image.dto;

import com.whitedelay.productshop.image.entity.ImageTableEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageInfoRequestDto {
    private ImageTableEnum imageTable;
    private Long imageTableId;

    public static ImageInfoRequestDto from (ImageTableEnum imageTable, Long imageTableId) {
        return ImageInfoRequestDto.builder()
                .imageTable(imageTable)
                .imageTableId(imageTableId)
                .build();
    }
}
