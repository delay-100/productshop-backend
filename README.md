ProductShop
---
상품 e-commerce 플랫폼

## 🖥️ 프로젝트 소개
- 사용자가 다양한 상품을 탐색하고 주문할 수 있는 e-commerce 플랫폼입니다.
- 관리자는 상품을 등록할 수 있으며, 상품 등록 시 이미지, 상품 옵션, 재고 등의 필수 정보를 입력해야 합니다.
- 회원가입 시 이메일 인증을 받아야 하며, 중복 이메일은 허용되지 않습니다. 회원가입 시 이름, 전화번호, 주소 등의 필수 정보를 입력하며, 개인정보는 암호화하여 저장됩니다.
- 모든 사용자는 로그인 여부와 관계없이 상품을 탐색할 수 있습니다.
- 로그인 및 로그아웃은 JWT 토큰을 이용해 DB 부하를 최소화했습니다.
- 회원은 여러 상품을 장바구니에 담아 단건 주문과 다건 주문 모두 가능합니다. 또한, 회원은 관심 있는 상품을 위시리스트에 추가할 수 있으며, 이 기능은 좋아요와 유사합니다.
- 사용자는 마이페이지에서 주소, 전화번호 및 비밀번호를 업데이트할 수 있습니다.
- 마이페이지에서 WishList와 주문한 상품의 상태를 조회할 수 있으며, 주문 취소 및 반품 기능도 제공합니다.
- 동시에 들어오는 트래픽을 처리하기 위한 **재고 동시성 제어 기능**을 구현하였습니다.

### 🕰️ 개발 기간 및 인원
* 기간: 24.06.19 - 24.07.16(1개월)
* 인원: 1인

### ⚙️ 개발 환경
![Java](https://img.shields.io/badge/Java-Oracle%20OpenJDK%2021.0.3-blue)
- **Framework**: Spring Boot 3.2.6
- **Database**: MySQL 8.4.0
- **In-Memory**: Redis 7.2.5
- **ORM**: Spring Data JPA 3.2.6
- **Security**: Spring Security 6.2.4, JWT
- **Cloud**: AWS S3
- **Tools**: Docker 26.1.1, GitHub Actions, JMeter

### 🏗️ 아키텍쳐
<!-- ![아키텍쳐](https://github.com/user-attachments/assets/10887e9c-ccd1-4fdd-8ffa-b8a11cfa943f) -->
<img src="https://github.com/user-attachments/assets/10887e9c-ccd1-4fdd-8ffa-b8a11cfa943f" height="400"/>

### 🗃️ ERD([ERDCloud](https://www.erdcloud.com/d/Xm7kDGmuH6picA6E8))
<!-- ![ERD](https://github.com/user-attachments/assets/8c43bb59-37d7-4d2d-81ab-4a949d93cce2) -->
<img src="https://github.com/user-attachments/assets/8c43bb59-37d7-4d2d-81ab-4a949d93cce2" height="400"/>


### 📚 API 문서
- #### [POSTMAN](https://documenter.getpostman.com/view/23481846/2sA3kSo3ZJ)

## 📌 주요기능([구현 이야기](https://delay100.tistory.com/198))

### [회원 가입](https://delay100.tistory.com/200)
- 이메일 인증(Naver SMTP, Redis) - 동작 프로세스 다이어그램으로 만들
- 개인 정보 암호화(AES256, BCryptPasswordEncoder)
  
### [로그인 및 인가](https://delay100.tistory.com/201)
- SpringSecurity
- JWT: 2개의 토큰(Access, Refresh)으로 관리
- Refresh Token 정보 저장(Redis)

### [상품](https://delay100.tistory.com/203)
- 상품 이미지 저장(AWS S3)
- 판매 가능 여부 업데이트(DB Scheduling)
- 옵션별 재고 캐싱(Redis)
- 상품 검색

### [주문](https://delay100.tistory.com/206)
- 재고 처리(DB 비관적 락, Redis)
- 주문 취소, 반품(DB Scheduling)
- 주문 전/후 내역 확인
  
### 그 외
- [마이페이지(개인정보 업데이트)](https://delay100.tistory.com/202?category=1149514)
- [장바구니, 위시리스트](https://delay100.tistory.com/205?category=1149514)
  
## 🔍 트러블슈팅
- [주문 상품 재고 DB의 동시성 문제 해결](https://delay100.tistory.com/208#2.%20%EB%8F%99%EC%8B%9C%EC%84%B1%20%EC%A0%9C%EC%96%B4-1)
- [Redis의 동시성 이슈 해결](https://delay100.tistory.com/208#3.%20Redis%20%EC%9E%AC%EA%B3%A0%EA%B4%80%EB%A6%AC-1)
<!-- - JWT를 사용함에도 불구하고 UserDetailsImpl에서 Member DB를 검사하여 세션 기반 인증과 차이가 없던 문제 해결(-> 당연한건디................) -->

## 📈 성능 최적화
- [상품 재고 관리를 Redis 캐싱으로 전환하여 주문 속도 개선](https://delay100.tistory.com/208#3.%20Redis%20%EC%9E%AC%EA%B3%A0%EA%B4%80%EB%A6%AC-1)
- [Redisson도입](https://delay100.tistory.com/208#4-1.%20Redissen-1) -> [Redis INCR 명령으로 변경](https://delay100.tistory.com/208#4-2.%20INC-1)하여 성능 최적화
  
![image](https://github.com/user-attachments/assets/99cb4c71-edef-4fbc-98b7-58f4e2ec7235)

## 📂 파일 구조
<details>
<summary>파일 구조 펼치기</summary>

```
📦 
├─ .github
│  └─ workflows
│     └─ gitaction.yml
├─ .gitignore
├─ Dockerfile
├─ README.md
├─ build.gradle
├─ docker-compose.yml
├─ gradle
│  └─ wrapper
│     ├─ gradle-wrapper.jar
│     └─ gradle-wrapper.properties
├─ gradlew
├─ gradlew.bat
├─ settings.gradle
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ com
   │  │     └─ whitedelay
   │  │        └─ productshop
   │  │           ├─ ProductshopApplication.java
   │  │           ├─ cart
   │  │           │  ├─ controller
   │  │           │  │  └─ CartController.java
   │  │           │  ├─ dto
   │  │           │  │  ├─ CartAllInfoResponseDto.java
   │  │           │  │  ├─ CartInfoRequestDto.java
   │  │           │  │  ├─ CartInfoResponseDto.java
   │  │           │  │  ├─ CartRequestDto.java
   │  │           │  │  └─ CartSimpleInfoRequestDto.java
   │  │           │  ├─ entity
   │  │           │  │  ├─ Cart.java
   │  │           │  │  └─ Timestamped.java
   │  │           │  ├─ repository
   │  │           │  │  └─ CartRepository.java
   │  │           │  └─ service
   │  │           │     └─ CartService.java
   │  │           ├─ exception
   │  │           │  └─ TokenCreationException.java
   │  │           ├─ image
   │  │           │  ├─ config
   │  │           │  │  └─ S3Config.java
   │  │           │  ├─ dto
   │  │           │  │  ├─ ImageInfoRequestDto.java
   │  │           │  │  ├─ ImageRequestDto.java
   │  │           │  │  └─ ImageResponseDto.java
   │  │           │  ├─ entity
   │  │           │  │  ├─ Image.java
   │  │           │  │  ├─ ImageTableEnum.java
   │  │           │  │  └─ Timestamped.java
   │  │           │  ├─ repository
   │  │           │  │  └─ ImageRepository.java
   │  │           │  └─ service
   │  │           │     └─ ImageService.java
   │  │           ├─ mail
   │  │           │  ├─ config
   │  │           │  │  └─ MailConfig.java
   │  │           │  ├─ controller
   │  │           │  │  └─ MailController.java
   │  │           │  ├─ dto
   │  │           │  │  ├─ SignupVerificationEmailDto.java
   │  │           │  │  └─ SignupVerifyCodeDto.java
   │  │           │  └─ service
   │  │           │     └─ MailService.java
   │  │           ├─ member
   │  │           │  ├─ controller
   │  │           │  │  ├─ AuthController.java
   │  │           │  │  └─ MemberController.java
   │  │           │  ├─ dto
   │  │           │  │  ├─ LoginRequestDto.java
   │  │           │  │  ├─ LoginResponseDto.java
   │  │           │  │  ├─ LogoutRequestDto.java
   │  │           │  │  ├─ MemberMyInfoRequestDto.java
   │  │           │  │  ├─ MemberMyInfoResponseDto.java
   │  │           │  │  ├─ MemberPasswordRequestDto.java
   │  │           │  │  ├─ MemberRequestDto.java
   │  │           │  │  ├─ OrderCancelResponseDto.java
   │  │           │  │  ├─ OrderDetailResponseDto.java
   │  │           │  │  ├─ OrderListResponseDto.java
   │  │           │  │  ├─ OrderProductDetailResponseDto.java
   │  │           │  │  ├─ OrderReturnResponseDto.java
   │  │           │  │  ├─ RefreshTokenRequestDto.java
   │  │           │  │  ├─ RefreshTokenResponseDto.java
   │  │           │  │  └─ SignupRequestDto.java
   │  │           │  ├─ entity
   │  │           │  │  ├─ Member.java
   │  │           │  │  ├─ MemberRoleEnum.java
   │  │           │  │  └─ Timestamped.java
   │  │           │  ├─ repository
   │  │           │  │  └─ MemberRepository.java
   │  │           │  ├─ service
   │  │           │  │  ├─ AuthService.java
   │  │           │  │  └─ MemberService.java
   │  │           │  └─ validation
   │  │           │     ├─ ZipCode.java
   │  │           │     └─ ZipCodeValidator.java
   │  │           ├─ order
   │  │           │  ├─ controller
   │  │           │  │  └─ OrderController.java
   │  │           │  ├─ dto
   │  │           │  │  ├─ DetuctedProductInfo.java
   │  │           │  │  ├─ OrderProductAllInfoRequestDto.java
   │  │           │  │  ├─ OrderProductAllInfoResponseDto.java
   │  │           │  │  ├─ OrderProductInfoRequestDto.java
   │  │           │  │  ├─ OrderProductPayRequestDto.java
   │  │           │  │  ├─ OrderProductPayResponseDto.java
   │  │           │  │  ├─ OrderProductRequestDto.java
   │  │           │  │  ├─ OrderProductResponseDto.java
   │  │           │  │  └─ OrderRequestDto.java
   │  │           │  ├─ entity
   │  │           │  │  ├─ Order.java
   │  │           │  │  ├─ OrderCardCompanyEnum.java
   │  │           │  │  ├─ OrderProduct.java
   │  │           │  │  ├─ OrderStatusEnum.java
   │  │           │  │  └─ Timestamped.java
   │  │           │  ├─ repository
   │  │           │  │  ├─ OrderProductRepository.java
   │  │           │  │  └─ OrderRepository.java
   │  │           │  └─ service
   │  │           │     ├─ OrderProductService.java
   │  │           │     ├─ OrderService.java
   │  │           │     └─ OrderStatusUpdateService.java
   │  │           ├─ product
   │  │           │  ├─ config
   │  │           │  │  └─ LoadDatabase.java
   │  │           │  ├─ controller
   │  │           │  │  └─ ProductController.java
   │  │           │  ├─ dto
   │  │           │  │  ├─ ProductDetailResponseDto.java
   │  │           │  │  ├─ ProductListResponseDto.java
   │  │           │  │  ├─ ProductOptionDetailResponseDto.java
   │  │           │  │  ├─ ProductOptionRequestDto.java
   │  │           │  │  ├─ ProductOptionResponseDto.java
   │  │           │  │  ├─ ProductOptionStockRequestDto.java
   │  │           │  │  ├─ ProductOptionStockResponseDto.java
   │  │           │  │  ├─ ProductRequestDto.java
   │  │           │  │  ├─ ProductResponseDto.java
   │  │           │  │  └─ ProductWithOptionsRequestDto.java
   │  │           │  ├─ entity
   │  │           │  │  ├─ Product.java
   │  │           │  │  ├─ ProductCategoryEnum.java
   │  │           │  │  ├─ ProductOption.java
   │  │           │  │  ├─ ProductStatusEnum.java
   │  │           │  │  └─ Timestamped.java
   │  │           │  ├─ repository
   │  │           │  │  ├─ ProductOptionRepository.java
   │  │           │  │  └─ ProductRepository.java
   │  │           │  └─ service
   │  │           │     ├─ ProductService.java
   │  │           │     └─ ProductStatusUpdateService.java
   │  │           ├─ redis
   │  │           │  ├─ config
   │  │           │  │  └─ RedisConfig.java
   │  │           │  └─ service
   │  │           │     └─ RedisService.java
   │  │           ├─ security
   │  │           │  ├─ UserDetails
   │  │           │  │  ├─ UserDetailsImpl.java
   │  │           │  │  └─ UserDetailsServiceImpl.java
   │  │           │  ├─ config
   │  │           │  │  ├─ EncryptConfig.java
   │  │           │  │  └─ WebSecurityConfig.java
   │  │           │  └─ jwt
   │  │           │     ├─ JwtAuthenticationFilter.java
   │  │           │     ├─ JwtAuthorizationFilter.java
   │  │           │     └─ JwtUtil.java
   │  │           ├─ util
   │  │           │  ├─ AES256Encoder.java
   │  │           │  └─ ApiResponse.java
   │  │           └─ wishlist
   │  │              ├─ controller
   │  │              │  └─ WishlistController.java
   │  │              ├─ dto
   │  │              │  ├─ WishlistRequestDto.java
   │  │              │  ├─ WishlistResponseDto.java
   │  │              │  └─ WishlistWishRequestDto.java
   │  │              ├─ entity
   │  │              │  ├─ Timestamped.java
   │  │              │  └─ Wishlist.java
   │  │              ├─ repository
   │  │              │  └─ WishlistRepository.java
   │  │              └─ service
   │  │                 └─ WishlistService.java
   │  └─ resources
   │     └─ application.yml
   └─ test
      └─ java
         └─ com
            └─ whitedelay
               └─ productshop
                  ├─ ProductshopApplicationTests.java
                  ├─ cart
                  │  ├─ controller
                  │  │  └─ CartControllerTest.java
                  │  └─ service
                  │     └─ CartServiceTest.java
                  ├─ mail
                  │  ├─ controller
                  │  │  └─ MailControllerTest.java
                  │  └─ service
                  │     └─ MailServiceTest.java
                  ├─ member
                  │  ├─ controller
                  │  │  ├─ AuthControllerTest.java
                  │  │  └─ MemberControllerTest.java
                  │  └─ service
                  │     ├─ AuthServiceTest.java
                  │     └─ MemberServiceTest.java
                  ├─ order
                  │  ├─ controller
                  │  │  └─ OrderControllerTest.java
                  │  └─ service
                  │     └─ OrderServiceTest.java
                  ├─ product
                  │  ├─ controller
                  │  │  └─ ProductControllerTest.java
                  │  └─ service
                  │     └─ ProductServiceTest.java
                  └─ wishlist
                     ├─ controller
                     │  └─ WishlistControllerTest.java
                     └─ service
                        └─ WishlistServiceTest.java

```

</details>
