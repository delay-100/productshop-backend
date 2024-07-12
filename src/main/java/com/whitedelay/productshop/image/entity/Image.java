package com.whitedelay.productshop.image.entity;

import com.whitedelay.productshop.image.dto.ImageRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ImageTableEnum imageTable;

    @Column(nullable = false)
    private Long imageTableId;

    @Column(nullable = false)
    private String imageFileName;

    public static Image from(ImageRequestDto image) {
        return Image.builder()
                .imageUrl(image.getImageUrl())
                .imageTable(image.getImageTable())
                .imageTableId(image.getImageTableId())
                .imageFileName(image.getImageFileName())
                .build();
    }
}
