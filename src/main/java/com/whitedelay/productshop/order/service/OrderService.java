package com.whitedelay.productshop.order.service;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.order.dto.OrderProductAllInfoRequestDto;
import com.whitedelay.productshop.order.dto.OrderProductAllInfoResponseDto;
import com.whitedelay.productshop.order.dto.OrderProductResponseDto;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import com.whitedelay.productshop.product.repository.ProductRepository;
import com.whitedelay.productshop.security.AES256Encoder;
import com.whitedelay.productshop.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final AES256Encoder aes256Encoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public OrderProductAllInfoResponseDto getOrderProductAllInfo(String userToken, OrderProductAllInfoRequestDto orderProductAllInfoRequestDto) {
        // 토큰 내의 유저 추출
        userToken = jwtUtil.substringToken(userToken);
        String memberId = jwtUtil.getMemberInfoFromToken(userToken).getSubject();
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

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
        int deliveryFee = totalOrderPrice >= 30000 ? 0 : 3000;
        int finalPrice = totalOrderPrice + deliveryFee;

        return OrderProductAllInfoResponseDto.builder()
                .orderMemberName(aes256Encoder.decodeString(member.getMemberName())) // 회원 이름
                .orderZipCode(aes256Encoder.decodeString(member.getZipCode())) // 회원 우편번호
                .orderAddress(aes256Encoder.decodeString(member.getAddress())) // 회원 주소
                .orderPhone(aes256Encoder.decodeString(member.getPhone())) // 회원 전화번호
                .orderProducts(orderProducts)
                .totalOrderPrice(totalOrderPrice)
                .deliveryFee(deliveryFee)
                .finalPrice(finalPrice)
                .build();
    }
}
