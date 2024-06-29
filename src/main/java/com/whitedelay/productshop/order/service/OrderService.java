package com.whitedelay.productshop.order.service;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.order.dto.*;
import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.order.entity.OrderCardCompanyEnum;
import com.whitedelay.productshop.order.entity.OrderProduct;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import com.whitedelay.productshop.order.repository.OrderProductRepository;
import com.whitedelay.productshop.order.repository.OrderRepository;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import com.whitedelay.productshop.product.repository.ProductOptionRepository;
import com.whitedelay.productshop.product.repository.ProductRepository;
import com.whitedelay.productshop.security.AES256Encoder;
import com.whitedelay.productshop.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final AES256Encoder aes256Encoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public OrderProductAllInfoResponseDto getOrderProductAllInfo(Member member, OrderProductAllInfoRequestDto orderProductAllInfoRequestDto) {
        // 각 주문 항목에 대해 상품 정보 조회
        List<OrderProductResponseDto> orderProducts = orderProductAllInfoRequestDto.getOrderProducts().stream().map(orderProduct -> {
            Product product = productRepository.findByProductId(orderProduct.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));

            ProductOption productOption = null;

            int productPrice = product.getProductPrice();
            int totalPrice = productPrice * orderProduct.getQuantity();

            if (orderProduct.getProductOptionId() != null) {
                productOption = product.getProductOptions().stream()
                        .filter(option -> option.getProductOptionId().equals(orderProduct.getProductOptionId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
                productPrice += (productOption.getProductOptionPrice() * orderProduct.getQuantity());
            }

            return OrderProductResponseDto.builder()
                    .productId(product.getProductId())
                    .productTitle(product.getProductTitle())
                    .quantity(orderProduct.getQuantity())
                    .productOptionId(productOption != null ? productOption.getProductOptionId() : null)
                    .productOptionName(productOption != null ? productOption.getProductOptionName() : null)
                    .productPrice(productPrice)
                    .totalPrice(totalPrice)
                    .build();
        }).collect(Collectors.toList());

        // 총 결제 금액 계산
        int totalOrderPrice = orderProducts.stream().mapToInt(OrderProductResponseDto::getTotalPrice).sum();
        int orderShippingFee = totalOrderPrice >= 30000 ? 0 : 3000;
        int orderPrice = totalOrderPrice + orderShippingFee;

        return OrderProductAllInfoResponseDto.builder()
                .orderMemberName(aes256Encoder.decodeString(member.getMemberName())) // 회원 이름
                .orderZipCode(aes256Encoder.decodeString(member.getZipCode())) // 회원 우편번호
                .orderAddress(aes256Encoder.decodeString(member.getAddress())) // 회원 주소
                .orderPhone(aes256Encoder.decodeString(member.getPhone())) // 회원 전화번호
                .orderProducts(orderProducts)
                .totalOrderPrice(totalOrderPrice)
                .orderShippingFee(orderShippingFee)
                .orderPrice(orderPrice)
                .build();
    }

    @Transactional
    public OrderProductPayResponseDto postOrderProductPay(Member member, OrderProductPayRequestDto requestDto) {
        // 주문 엔티티 생성 및 저장
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .orderDate(LocalDateTime.now()) // 주문 날짜를 현재 시각으로 설정
                .orderStatus(OrderStatusEnum.PAYING) // 초기 주문 상태를 결제 중으로 설정
                .orderPayYN(false) // 결제 완료 여부를 false로 설정
                .orderShippingFee(requestDto.getOrderShippingFee()) // 배송비 설정
                .orderPrice(requestDto.getOrderPrice()) // 주문 총 가격 설정
                .orderCardCompany(OrderCardCompanyEnum.valueOf(requestDto.getOrderCardCompany().toUpperCase())) // 카드 회사 설정
                .orderMemberName(requestDto.getOrderMemberName()) // 주문자 이름 설정
                .orderZipCode(requestDto.getOrderZipCode()) // 주문자 우편번호 설정
                .orderAddress(requestDto.getOrderAddress()) // 주문자 주소 설정
                .orderPhone(requestDto.getOrderPhone()) // 주문자 전화번호 설정
                .orderReq(requestDto.getOrderReq()) // 배송 요청사항 설정
                .member(member) // 주문자 정보를 member 객체로 설정
                .build();

        Order order = Order.from(orderRequestDto); // OrderRequestDto 객체를 Order 엔티티로 변환
        orderRepository.save(order); // Order 엔티티를 데이터베이스에 저장

        List<OrderProduct> orderProducts = new ArrayList<>(); // try 블록 외부에 orderProducts 변수 선언

        try {
            // 주문 상품 엔티티 생성 및 저장
            orderProducts = requestDto.getOrderProducts().stream().map(orderProductDto -> {
                Product product = productRepository.findById(orderProductDto.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다.")); // 상품 조회

                ProductOption productOption = null;
                int optionPrice = 0;
                if (orderProductDto.getProductOptionId() != null) {
                    productOption = product.getProductOptions().stream()
                            .filter(option -> option.getProductOptionId().equals(orderProductDto.getProductOptionId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다.")); // 상품 옵션 조회
                    if (productOption.getProductOptionStock() < orderProductDto.getQuantity()) {
                        throw new IllegalArgumentException("상품 옵션의 재고가 부족합니다.");
                    }
                    optionPrice = productOption.getProductOptionPrice(); // 상품 옵션 가격 설정
                } else {
                    if (product.getProductStock() < orderProductDto.getQuantity()) {
                        throw new IllegalArgumentException("상품의 재고가 부족합니다.");
                    }
                }

                return OrderProduct.from(OrderProductRequestDto.builder()
                        .order(order) // 주문 정보 설정
                        .product(product) // 상품 정보 설정
                        .orderProductQuantity(orderProductDto.getQuantity()) // 주문 상품 수량 설정
                        .orderProductPrice(product.getProductPrice()) // 주문 상품 가격 설정
                        .orderProductOptionId(productOption != null ? productOption.getProductOptionId() : 0) // 주문 상품 옵션 설정
                        .orderProductOptionPrice(optionPrice) // 주문 상품 옵션 가격 설정
                        .build()); // OrderProduct 엔티티 생성
            }).collect(Collectors.toList());

            // 재고 업데이트
            for (OrderProduct orderProduct : orderProducts) {
                Product product = orderProduct.getProduct();
                if (orderProduct.getOrderProductOptionId() != 0) {
                    ProductOption productOption = productOptionRepository.findById(orderProduct.getOrderProductOptionId())
                            .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
                    productOption.setProductOptionStock(productOption.getProductOptionStock() - orderProduct.getOrderProductQuantity());
                    productOptionRepository.save(productOption);
                }
                product.setProductStock(product.getProductStock() - orderProduct.getOrderProductQuantity());
                productRepository.save(product);
            }

            orderProductRepository.saveAll(orderProducts); // OrderProduct 엔티티를 데이터베이스에 저장

            // 결제 성공 시 주문 상태 업데이트
            order.setOrderStatus(OrderStatusEnum.PAYMENT_COMPLETED); // 주문 상태를 결제 완료로 업데이트
            order.setOrderPayYN(true); // 결제 완료 여부를 true로 설정
            orderRepository.save(order); // 업데이트된 주문 정보를 데이터베이스에 저장

            return OrderProductPayResponseDto.builder()
                    .orderMemberName(requestDto.getOrderMemberName()) // 응답 DTO에 주문자 이름 설정
                    .orderZipCode(requestDto.getOrderZipCode()) // 응답 DTO에 주문자 우편번호 설정
                    .orderAddress(requestDto.getOrderAddress()) // 응답 DTO에 주문자 주소 설정
                    .orderPhone(requestDto.getOrderPhone()) // 응답 DTO에 주문자 전화번호 설정
                    .orderReq(requestDto.getOrderReq()) // 응답 DTO에 배송 요청사항 설정
                    .orderCardCompany(requestDto.getOrderCardCompany()) // 응답 DTO에 카드 회사 설정
                    .totalOrderPrice(requestDto.getTotalOrderPrice()) // 응답 DTO에 총 주문 가격 설정
                    .orderShippingFee(requestDto.getOrderShippingFee()) // 응답 DTO에 배송비 설정
                    .orderPrice(requestDto.getOrderPrice()) // 응답 DTO에 최종 주문 가격 설정
                    .paymentStatus(OrderStatusEnum.Status.PAYMENT_COMPLETED) // 결제 성공 상태 설정
                    .build();

        } catch (Exception e) {
            // 결제 실패 시 주문 상태 업데이트 및 재고 원상 복구
            for (OrderProduct orderProduct : orderProducts) {
                Product product = orderProduct.getProduct();
                if (orderProduct.getOrderProductOptionId() != 0) {
                    ProductOption productOption = productOptionRepository.findById(orderProduct.getOrderProductOptionId())
                            .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
                    productOption.setProductOptionStock(productOption.getProductOptionStock() + orderProduct.getOrderProductQuantity());
                    productOptionRepository.save(productOption);
                }
                product.setProductStock(product.getProductStock() + orderProduct.getOrderProductQuantity());
                productRepository.save(product);
            }

            order.setOrderStatus(OrderStatusEnum.PAYMENT_FAILED); // 주문 상태를 결제 실패로 업데이트
            orderRepository.save(order); // 업데이트된 주문 정보를 데이터베이스에 저장

            return OrderProductPayResponseDto.builder()
                    .orderMemberName(requestDto.getOrderMemberName()) // 응답 DTO에 주문자 이름 설정
                    .orderZipCode(requestDto.getOrderZipCode()) // 응답 DTO에 주문자 우편번호 설정
                    .orderAddress(requestDto.getOrderAddress()) // 응답 DTO에 주문자 주소 설정
                    .orderPhone(requestDto.getOrderPhone()) // 응답 DTO에 주문자 전화번호 설정
                    .orderReq(requestDto.getOrderReq()) // 응답 DTO에 배송 요청사항 설정
                    .orderCardCompany(requestDto.getOrderCardCompany()) // 응답 DTO에 카드 회사 설정
                    .totalOrderPrice(requestDto.getTotalOrderPrice()) // 응답 DTO에 총 주문 가격 설정
                    .orderShippingFee(requestDto.getOrderShippingFee()) // 응답 DTO에 배송비 설정
                    .orderPrice(requestDto.getOrderPrice()) // 응답 DTO에 최종 주문 가격 설정
                    .paymentStatus(OrderStatusEnum.Status.PAYMENT_FAILED) // 결제 실패 상태 설정
                    .build();
        }
    }

    }


