package com.whitedelay.productshop.order.service;

import com.whitedelay.productshop.member.dto.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final AES256Encoder aes256Encoder;

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
        Map<OrderProduct, Integer> originalProductStocks = new HashMap<>(); // 원래 product 재고를 기록할 맵
        Map<OrderProduct, Integer> originalOptionStocks = new HashMap<>(); // 원래 product option 재고를 기록할 맵

        for (OrderProductResponseDto orderProductDto : requestDto.getOrderProducts()) {
            Product product = productRepository.findById(orderProductDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다.")); // 상품 조회

            ProductOption productOption = null;
            int optionPrice = 0;
            if (orderProductDto.getProductOptionId() != null) {
                productOption = product.getProductOptions().stream()
                        .filter(option -> option.getProductOptionId().equals(orderProductDto.getProductOptionId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다.")); // 상품 옵션 조회
                optionPrice = productOption.getProductOptionPrice(); // 상품 옵션 가격 설정
            }

            OrderProduct orderProduct = OrderProduct.from(OrderProductRequestDto.builder()
                    .order(order) // 주문 정보 설정
                    .product(product) // 상품 정보 설정
                    .orderProductQuantity(orderProductDto.getQuantity()) // 주문 상품 수량 설정
                    .orderProductPrice(product.getProductPrice()) // 주문 상품 가격 설정
                    .orderProductOptionId(productOption != null ? productOption.getProductOptionId() : 0) // 주문 상품 옵션 설정
                    .orderProductOptionPrice(optionPrice) // 주문 상품 옵션 가격 설정
                    .build());

            orderProducts.add(orderProduct);
        }

        orderProductRepository.saveAll(orderProducts); // OrderProduct 엔티티를 데이터베이스에 저장

        try {
            // 재고 업데이트
            for (OrderProduct orderProduct : orderProducts) {
                Product product = orderProduct.getProduct();
                originalProductStocks.put(orderProduct, product.getProductStock()); // 원래 product 재고 기록
                product.setProductStock(product.getProductStock() - orderProduct.getOrderProductQuantity()); // product 재고 감소

                if (orderProduct.getOrderProductOptionId() != 0) {
                    ProductOption productOption = productOptionRepository.findById(orderProduct.getOrderProductOptionId())
                            .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
                    originalOptionStocks.put(orderProduct, productOption.getProductOptionStock()); // 원래 product option 재고 기록
                    if (productOption.getProductOptionStock() < orderProduct.getOrderProductQuantity()) {
                        throw new IllegalArgumentException("상품 옵션의 재고가 부족합니다.");
                    }
                    productOption.setProductOptionStock(productOption.getProductOptionStock() - orderProduct.getOrderProductQuantity());
                    productOptionRepository.save(productOption);
                } else {
                    if (product.getProductStock() < orderProduct.getOrderProductQuantity()) {
                        throw new IllegalArgumentException("상품의 재고가 부족합니다.");
                    }
                    productRepository.save(product);
                }
            }

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
                if (originalProductStocks.containsKey(orderProduct)) {
                    product.setProductStock(originalProductStocks.get(orderProduct)); // product 재고 원상 복구
                    productRepository.save(product);
                }

                if (orderProduct.getOrderProductOptionId() != 0) {
                    ProductOption productOption = productOptionRepository.findById(orderProduct.getOrderProductOptionId())
                            .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
                    if (originalOptionStocks.containsKey(orderProduct)) {
                        productOption.setProductOptionStock(originalOptionStocks.get(orderProduct)); // product option 재고 원상 복구
                        productOptionRepository.save(productOption);
                    }
                }
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


    /**
     * 멤버의 주문 목록을 페이지네이션하여 조회하는 메서드
     *
     * @param member 멤버 객체
     * @param page     페이지 번호 (0부터 시작)
     * @param size     페이지 당 주문 수
     * @return 주문 목록 페이지
     */
    @Transactional(readOnly = true)
    public Page<OrderListResponseDto> getOrderList(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByMemberMemberId(member.getMemberId(), pageable);

        return orders.map(order -> {
            List<OrderProduct> orderProducts = orderProductRepository.findByOrderOrderId(order.getOrderId());
            String productTitle = orderProducts.stream()
                    .map(orderProduct -> productRepository.findByProductId(orderProduct.getProduct().getProductId())
                            .map(Product::getProductTitle)
                            .orElse("Unknown Product"))
                    .findFirst()
                    .orElse("Unknown Product");
            int orderProductCount = orderProducts.size(); // 총 orderProduct 수

            return OrderListResponseDto.from(order, productTitle, orderProductCount);
        });
    }

    /**
     * 멤버의 주문 상세 정보를 조회하는 메서드
     *
     * @param member 멤버 객체
     * @param orderId 주문 ID
     * @return 주문 상세 정보
     * @throws IllegalArgumentException 주문이 존재하지 않을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetail(Member member, Long orderId) {
        Order order = orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for memberId: " + member.getMemberId() + " and orderId: " + orderId);
        }

        List<OrderProduct> orderProducts = orderProductRepository.findByOrderOrderId(orderId);
        if (orderProducts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No OrderProducts found for orderId: " + orderId);
        }

        List<OrderProductDetailResponseDto> orderProductDetailResponseDto = orderProducts.stream()
                .map(orderProduct -> {
                    Product product = productRepository.findByProductId(orderProduct.getProduct().getProductId()).orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));

                    Optional<ProductOption> productOption = productOptionRepository.findByProductOptionId(orderProduct.getOrderProductOptionId());
                    String productOptionName = productOption.map(ProductOption::getProductOptionName).orElse("Unknown Option");
                    return OrderProductDetailResponseDto.from(orderProduct, product.getProductTitle(), productOptionName);
                }).collect(Collectors.toList());

        return OrderDetailResponseDto.from(order, orderProductDetailResponseDto);
    }


    /**
     * 멤버의 주문 상태를 취소로 업데이트하는 메서드
     *
     * @param member 멤버 객체
     * @param orderId 주문 ID
     * @return 주문 취소 응답 DTO
     */
    @Transactional
    public OrderCancelResponseDto updateOrderStatusCancel(Member member, Long orderId) {
        Order order = orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for memberId: " + member.getMemberId() + " and orderId: " + orderId);
        }

        // 취소 가능 상태인지 확인
        if (!order.getOrderStatus().isCancellable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel order after it has been shipped.");
        }

        order.setOrderStatus(OrderStatusEnum.ORDER_CANCELLED);

        List<OrderProduct> orderProducts = orderProductRepository.findByOrderOrderId(orderId);
        for (OrderProduct orderProduct : orderProducts) {
            Product product = orderProduct.getProduct();
            product.setProductStock(product.getProductStock() + orderProduct.getOrderProductQuantity());
            productRepository.save(product);
        }

        orderRepository.save(order);

        return OrderCancelResponseDto.from(order);
    }


    /**
     * 멤버의 주문 상태를 취소로 업데이트하는 메서드
     *
     * @param member 멤버 객체
     * @param orderId 주문 ID
     * @return 주문 취소 응답 DTO
     */
    @Transactional
    public OrderReturnResponseDto updateOrderStatusReturn(Member member, Long orderId) {
        Order order = orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for memberId: " + member.getMemberId() + " and orderId: " + orderId);
        }

        // 반품 가능 상태인지 확인
        if (!order.getOrderStatus().isReturnable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only delivered orders can be returned.");
        }

        // 반품 가능 기간인지 확인 (배송 완료 후 1일 이내)
        if (order.getUpdatedAt().plusMinutes(1).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return period has expired.");
        }

        order.setOrderStatus(OrderStatusEnum.RETURN_REQUESTED);
        orderRepository.save(order);

        return OrderReturnResponseDto.from(order);
    }

}


