//package com.whitedelay.productshop.product.config;
//
//import com.whitedelay.productshop.product.entity.Product;
//import com.whitedelay.productshop.product.entity.ProductCategoryEnum;
//import com.whitedelay.productshop.product.entity.ProductOption;
//import com.whitedelay.productshop.product.entity.ProductStatusEnum;
//import com.whitedelay.productshop.product.repository.ProductRepository;
//import com.whitedelay.productshop.product.repository.ProductOptionRepository;
//import com.whitedelay.productshop.redis.service.RedisService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@RequiredArgsConstructor
//public class LoadDatabase {
//    private final ProductRepository productRepository;
//    private final ProductOptionRepository productOptionRepository;
//    private final RedisService redisService; // RedisService 주입
//
//    @Bean
//    CommandLineRunner initDatabase() {
//        return args -> {
//            redisService.clearAllProductOptions();
//
//            LocalDateTime startDate1 = LocalDateTime.of(2024, 6, 26, 9, 0);
//            LocalDateTime startDate2 = LocalDateTime.of(2024, 6, 26, 10, 0);
//            LocalDateTime startDate3 = LocalDateTime.of(2024, 6, 27, 11, 0); // 새로운 시작 날짜 추가
//            LocalDateTime startDate4 = LocalDateTime.of(2024, 7, 7, 1, 0); // 새로운 시작 날짜 추가
//            LocalDateTime startDate5 = LocalDateTime.of(2024, 7, 12, 1, 38); // COMING_SOON 시작 날짜 추가
//
//            // 옵션이 있는 상품
//            Product product1 = Product.builder()
//                    .productTitle("반팔")
//                    .productContent("여름용 반팔 티셔츠")
//                    .productStatus(ProductStatusEnum.AVAILABLE) // Enum 값으로 변경
//                    .productWishlistCount(0)
//                    .productPrice(41100)
//                    .productCategory(ProductCategoryEnum.CLOTHING)
//                    .productStartDate(startDate1)
//                    .build();
//
//            Product product2 = Product.builder()
//                    .productTitle("하만 카메라")
//                    .productContent("하만 카메라 제품")
//                    .productStatus(ProductStatusEnum.AVAILABLE) // Enum 값으로 변경
//                    .productWishlistCount(0)
//                    .productPrice(22900)
//                    .productCategory(ProductCategoryEnum.ELECTRONICS)
//                    .productStartDate(startDate2)
//                    .build();
//
//            // 옵션이 없는 상품
//            Product product3 = Product.builder()
//                    .productTitle("노트북")
//                    .productContent("갤럭시북3")
//                    .productStatus(ProductStatusEnum.AVAILABLE) // Enum 값으로 변경
//                    .productWishlistCount(0)
//                    .productPrice(1500000)
//                    .productCategory(ProductCategoryEnum.ELECTRONICS)
//                    .productStartDate(startDate3)
//                    .build();
//
//            // COMING_SOON 상태의 상품
//            Product product4 = Product.builder()
//                    .productTitle("스마트워치")
//                    .productContent("최신 스마트워치")
//                    .productStatus(ProductStatusEnum.COMING_SOON)
//                    .productWishlistCount(0)
//                    .productPrice(250000)
//                    .productCategory(ProductCategoryEnum.ELECTRONICS)
//                    .productStartDate(startDate5)
//                    .build();
//
//            productRepository.saveAll(Arrays.asList(product1, product2, product3, product4));
//
//            ProductOption option1 = ProductOption.builder()
//                    .product(product1)
//                    .productOptionTitle("화이트/ S")
//                    .productOptionStock(100000)
//                    .productOptionPrice(100)
//                    .build();
//
//            ProductOption option2 = ProductOption.builder()
//                    .product(product1)
//                    .productOptionTitle("블랙/ S")
//                    .productOptionStock(200000)
//                    .productOptionPrice(200)
//                    .build();
//
//            ProductOption option3 = ProductOption.builder()
//                    .product(product1)
//                    .productOptionTitle("블랙/ M")
//                    .productOptionStock(300000)
//                    .productOptionPrice(300)
//                    .build();
//
//            ProductOption option4 = ProductOption.builder()
//                    .product(product2)
//                    .productOptionTitle("XP2_RED")
//                    .productOptionStock(400000)
//                    .productOptionPrice(10)
//                    .build();
//
//            ProductOption option5 = ProductOption.builder()
//                    .product(product2)
//                    .productOptionTitle("HP5")
//                    .productOptionStock(500000)
//                    .productOptionPrice(30)
//                    .build();
//
//            ProductOption option6 = ProductOption.builder()
//                    .product(product2)
//                    .productOptionTitle("XP2 B&W")
//                    .productOptionStock(600000)
//                    .productOptionPrice(50)
//                    .build();
//
//
//            ProductOption option7 = ProductOption.builder()
//                    .product(product3)
//                    .productOptionTitle("기본")
//                    .productOptionStock(700000)
//                    .productOptionPrice(0)
//                    .build();
//
//            ProductOption option8 = ProductOption.builder()
//                    .product(product4)
//                    .productOptionTitle("실버")
//                    .productOptionStock(10000)
//                    .productOptionPrice(0)
//                    .build();
//
//            ProductOption option9 = ProductOption.builder()
//                    .product(product4)
//                    .productOptionTitle("골드")
//                    .productOptionStock(15000)
//                    .productOptionPrice(0)
//                    .build();
//
//            productOptionRepository.saveAll(Arrays.asList(option1, option2, option3, option4, option5, option6, option7, option8, option9));
//
//            saveProductOptionStockToRedis(Arrays.asList(option1, option2, option3, option4, option5, option6, option7, option8, option9));
//        };
//    }
//
//    private void saveProductOptionStockToRedis(List<ProductOption> options) {
//        for (ProductOption option : options) {
//            redisService.setInitialStock(option.getProductOptionId(), option.getProductOptionStock());
//        }
//    }
//}
