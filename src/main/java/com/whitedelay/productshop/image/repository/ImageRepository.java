package com.whitedelay.productshop.image.repository;

import com.whitedelay.productshop.image.entity.Image;
import com.whitedelay.productshop.image.entity.ImageTableEnum;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByImageTableAndImageTableId(ImageTableEnum imageTableEnum, Long productId);

    Optional<Image> findFirstByImageTableAndImageTableId(ImageTableEnum imageTable,
                                                         Long imageTableId, PageRequest pageRequest);

}
