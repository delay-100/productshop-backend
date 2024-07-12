package com.whitedelay.productshop.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.whitedelay.productshop.image.dto.ImageRequestDto;
import com.whitedelay.productshop.image.dto.ImageResponseDto;
import com.whitedelay.productshop.image.dto.ImageInfoRequestDto;
import com.whitedelay.productshop.image.entity.Image;
import com.whitedelay.productshop.image.entity.ImageTableEnum;
import com.whitedelay.productshop.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final AmazonS3 amazonS3;

    @Value("${AWS_S3_BUCKET}")
    private String bucket;

    @Transactional
    public ImageResponseDto uploadSingleImage(ImageInfoRequestDto imageInfoRequestDto, MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename(); // 파일 이름에서 공백 제거한 새로운 파일이름 생성
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");
        String fileName = imageInfoRequestDto.getImageTable() + "/" + uniqueFileName;

        File uploadFile = convert(multipartFile);

        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);

        imageRepository.save(Image.from(ImageRequestDto.from(
                uploadImageUrl,
                imageInfoRequestDto.getImageTable(),
                imageInfoRequestDto.getImageTableId(),
                fileName
        )));

        return ImageResponseDto.builder()
                .uploadImageUrl(uploadImageUrl)
                .fileName(originalFileName)
                .build();
    }

    public List<ImageResponseDto> uploadMultiImage(ImageInfoRequestDto imageInfoRequestDto, List<MultipartFile> multipartFileList) {
        return multipartFileList.stream()
                .map(file -> {
                    try {
                        return uploadSingleImage(imageInfoRequestDto, file);
                    } catch (IOException e) {
                        throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
                    }
                })
                .toList();
    }

    private File convert(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        File convertFile = new File(uniqueFileName);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)){
                fos.write(file.getBytes());
            } catch (IOException e) {
                throw e;
            }
            return convertFile;
        }
        throw new IllegalArgumentException(String.format("파일 변환에 실패했습니다. %s", originalFileName));
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일 삭제 성공");
        } else {
            log.info("파일 삭제 실패");
        }
    }

    public List<ImageResponseDto> findImageResponseList(ImageTableEnum imageTableEnum, Long productId) {
        return imageRepository.findByImageTableAndImageTableId(imageTableEnum, productId).stream()
                .map(image -> ImageResponseDto.from(image.getImageUrl(), image.getImageFileName()))
                .toList();
    }

    public ImageResponseDto findImageResponse(ImageTableEnum imageTableEnum, Long productId) {
        return imageRepository.findFirstByImageTableAndImageTableId(imageTableEnum, productId, PageRequest.of(0, 1))
                .map(image -> ImageResponseDto.builder()
                        .uploadImageUrl(image.getImageUrl())
                        .fileName(image.getImageFileName())
                        .build())
                .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));
    }
}
