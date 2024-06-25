package com.whitedelay.productshop.product.config;

import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductCategoryEnum;
import com.whitedelay.productshop.product.entity.ProductOption;
import com.whitedelay.productshop.product.repository.ProductRepository;
import com.whitedelay.productshop.product.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class LoadDatabase {
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            LocalDateTime startDate1 = LocalDateTime.of(2024, 6, 26, 9, 0);
            LocalDateTime startDate2 = LocalDateTime.of(2024, 6, 26, 10, 0);

            Product product1 = Product.builder()
                    .productTitle("반팔")
                    .productContent("여름용 반팔 티셔츠")
                    .productStatus("Available")
                    .productWishlistCount(0)
                    .productPrice(41100)
                    .productCategory(ProductCategoryEnum.CLOTHING)
                    .productStartDate(startDate1) // 설정된 시간
                    .build();

            Product product2 = Product.builder()
                    .productTitle("하만 카메라")
                    .productContent("하만 카메라 제품")
                    .productStatus("Available")
                    .productWishlistCount(0)
                    .productPrice(22900)
                    .productCategory(ProductCategoryEnum.ELECTRONICS)
                    .productStartDate(startDate2) // 설정된 시간
                    .build();

            productRepository.saveAll(Arrays.asList(product1, product2));

            ProductOption option1 = ProductOption.builder()
                    .product(product1)
                    .productOptionName("화이트/ S")
                    .productOptionStock(10)
                    .productOptionPrice(0)
                    .productStartDate(startDate1) // 설정된 시간
                    .build();

            ProductOption option2 = ProductOption.builder()
                    .product(product1)
                    .productOptionName("블랙/ S")
                    .productOptionStock(5)
                    .productOptionPrice(0)
                    .productStartDate(startDate1) // 설정된 시간
                    .build();

            ProductOption option3 = ProductOption.builder()
                    .product(product1)
                    .productOptionName("블랙/ M")
                    .productOptionStock(5)
                    .productOptionPrice(0)
                    .productStartDate(startDate1) // 설정된 시간
                    .build();

            ProductOption option4 = ProductOption.builder()
                    .product(product2)
                    .productOptionName("XP2_RED")
                    .productOptionStock(20)
                    .productOptionPrice(0)
                    .productStartDate(startDate2) // 설정된 시간
                    .build();

            ProductOption option5 = ProductOption.builder()
                    .product(product2)
                    .productOptionName("HP5")
                    .productOptionStock(20)
                    .productOptionPrice(0)
                    .productStartDate(startDate2) // 설정된 시간
                    .build();

            ProductOption option6 = ProductOption.builder()
                    .product(product2)
                    .productOptionName("XP2 B&W")
                    .productOptionStock(20)
                    .productOptionPrice(0)
                    .productStartDate(startDate2) // 설정된 시간
                    .build();

            productOptionRepository.saveAll(Arrays.asList(option1, option2, option3, option4, option5, option6));
        };
    }
}
