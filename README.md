ProductShop
---
상품 구매 사이트

## 🖥️ 프로젝트 소개
7/19 추가 예정

### 🕰️ 개발 기간
* 24.06.19 - 24.07.16(1개월)

### ⚙️ 개발 환경
- `JAVA 21`
- `Oracle OpenJDK 21.0.3`
- **Framework**: Spring Boot 3.2.6
- **Database**: MySQL 8.4.0
- **ORM**: Spring Data JPA
- **Security**: Spring Security, JWT
- **Cloud**: AWS S3, Redis 7.2.5
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
- 이메일 인증(SMTP, Redis)
- 개인 정보 암호화(AES256, BCryptPasswordEncoder)
  
### 로그인 및 인가
- SpringSecurity
- JWT Token: 2개의 토큰(Access, Refresh)으로 관리
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

## 📈 성능 최적화 및 트러블슈팅
- 7/19 추가 예정
