ProductShop
---
상품 e-commerce 플랫폼

## 🖥️ 프로젝트 소개
- 사용자가 다양한 상품을 탐색하고 주문할 수 있는 e-commerce 플랫폼입니다.
- 관리자는 상품을 등록할 수 있으며, 상품 등록 시 이미지, 상품 옵션, 재고 등의 필수 정보를 입력해야 합니다.
- 회원가입 시 이메일 인증을 받아야 하며, 중복 이메일은 허용되지 않습니다. 모든 사용자는 로그인 여부와 관계없이 상품을 탐색할 수 있습니다.
- 회원은 여러 상품을 장바구니에 담아둘 수 있으며, 단건 주문과 다건 주문 모두 가능합니다. 또한, 회원은 관심 있는 상품을 위시리스트에 추가할 수 있으며, 이 기능은 좋아요와 유사합니다.

### 🕰️ 개발 기간 및 인원
* 기간: 24.06.19 - 24.07.16(1개월)
* 인원: 1인

### ⚙️ 개발 환경
- `JAVA 21`
- `Oracle OpenJDK 21.0.3`
- **Framework**: Spring Boot 3.2.6
- **Database**: MySQL 8.4.0, Redis 7.2.5
- **ORM**: Spring Data JPA
- **Security**: Spring Security, JWT
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

## 📌 주요기능
### 회원 가입
- 이메일 인증(Naver SMTP, Redis)
- 개인 정보 암호화(AES256, BCryptPasswordEncoder)
  
### 로그인 및 인가
- SpringSecurity
- JWT: 2개의 토큰(Access, Refresh)으로 관리
- Refresh Token 정보 저장(Redis)

### 상품
- 상품 이미지 저장(AWS S3)
- 판매 가능 여부 업데이트(DB Scheduling)
- 옵션별 재고 캐싱(Redis)
- 상품 검색

### 주문
- 재고 처리(DB Lock, Redis)
- 주문 취소, 반품(DB Scheduling)
- 주문 전/후 내역 확인
  
### 그 외
- 마이페이지(개인정보 업데이트)
- 장바구니
- 위시리스트
  
## 🔍 트러블슈팅
- 주문 상품 재고 DB의 동시성 문제 해결
- Redis의 동시성 이슈 해결
- JWT를 사용함에도 불구하고 UserDetailsImpl에서 Member DB를 검사하여 세션 기반 인증과 차이가 없던 문제 해결

## 📈 성능 최적화
- 상품 재고 관리를 Redis 캐싱으로 전환하여 주문 속도 개선
- Redisson에서 Redis INCR 명령으로 변경하여 성능 최적화
  

