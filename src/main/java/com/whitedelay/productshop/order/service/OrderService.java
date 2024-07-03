package com.whitedelay.productshop.order.service;

import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.member.entity.Member;
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
            Product product = productRepository.findById(orderProduct.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));

            ProductOption productOption = null;

            int productPrice = product.getProductPrice();
            int productOptionPrice = 0;
            if (orderProduct.getProductOptionId() != null) {
                productOption = productOptionRepository.findById(orderProduct.getProductOptionId())
                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
                productOptionPrice = productOption.getProductOptionPrice();
            }

            int productTotalPrice = (productPrice + productOptionPrice) * orderProduct.getQuantity();

            return OrderProductResponseDto.from(product, orderProduct.getQuantity(), productOption, productPrice, productTotalPrice);
        }).collect(Collectors.toList());

        // 총 결제 금액 계산
        int productTotalPrice = orderProducts.stream().mapToInt(OrderProductResponseDto::getProductTotalPrice).sum();
        int orderShippingFee = productTotalPrice >= 30000 ? 0 : 3000;
        int orderPrice = productTotalPrice + orderShippingFee;

        return OrderProductAllInfoResponseDto.from(member ,aes256Encoder, orderProducts, productTotalPrice, orderShippingFee, orderPrice);
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
                .orderMemberName(aes256Encoder.encodeString(requestDto.getOrderMemberName())) // 주문자 이름 설정
                .orderZipCode(requestDto.getOrderZipCode()) // 주문자 우편번호 설정
                .orderAddress(aes256Encoder.encodeString(requestDto.getOrderAddress())) // 주문자 주소 설정
                .orderPhone(aes256Encoder.encodeString(requestDto.getOrderPhone())) // 주문자 전화번호 설정
                .orderReq(aes256Encoder.encodeString(requestDto.getOrderReq())) // 배송 요청사항 설정
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
            if (orderProductDto.getProductOptionId() != null) {
                productOption = productOptionRepository.findById(orderProductDto.getProductOptionId())
                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다.")); // 상품 옵션 조회
            }

            OrderProduct orderProduct = OrderProduct.from(OrderProductRequestDto.builder()
                    .order(order) // 주문 정보 설정
                    .product(product) // 상품 정보 설정
                    .orderProductQuantity(orderProductDto.getQuantity()) // 주문 상품 수량 설정
                    .orderProductPrice(product.getProductPrice()) // 주문 상품 가격 설정
                    .orderProductOptionId(productOption != null ? productOption.getProductOptionId() : 0) // 주문 상품 옵션 설정
                    .orderProductOptionPrice(productOption != null ? productOption.getProductOptionPrice() : 0) // 주문 상품 옵션 가격 설정
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
                    .totalOrderPrice(requestDto.getTotalOrderPrice())
                    .orderShippingFee(requestDto.getOrderShippingFee())
                    .orderPrice(requestDto.getOrderPrice())
                    .paymentStatus(OrderStatusEnum.Status.PAYMENT_COMPLETED)
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
                    .totalOrderPrice(requestDto.getTotalOrderPrice())
                    .orderShippingFee(requestDto.getOrderShippingFee())
                    .orderPrice(requestDto.getOrderPrice())
                    .paymentStatus(OrderStatusEnum.Status.PAYMENT_FAILED) // 결제 실패 상태 설정
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public Page<OrderListResponseDto> getOrderList(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByMemberMemberId(member.getMemberId(), pageable);

        return orders.map(order -> {
            List<OrderProduct> orderProducts = orderProductRepository.findByOrderOrderId(order.getOrderId());

            if (orderProducts.isEmpty()) {
                throw new IllegalArgumentException("해당 주문에 대한 상품이 없습니다.");
            }

            // 묶음 상품에 대해서 대표 상품에 대한 옵션만 출력함
            Product product = productRepository.findById(orderProducts.getFirst().getProduct().getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));
            String productTitle = product.getProductTitle();
            int orderProductCount = orderProducts.size(); // 총 orderProduct 수

            return OrderListResponseDto.from(order, productTitle, orderProductCount);
        });
    }

    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetail(Member member, Long orderId) {
        Order order = orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 없습니다."));

        List<OrderProduct> orderProducts = orderProductRepository.findByOrderOrderId(orderId);
        if (orderProducts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "잘못된 주문이 존재:" + orderId);
        }
        List<OrderProductDetailResponseDto> orderProductDetailResponseDto = orderProducts.stream()
                .map(orderProduct -> {
                    Product product = productRepository.findById(orderProduct.getProduct().getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));

                    String productOptionTitle = null;
                    if (orderProduct.getOrderProductOptionId() != 0) {
                        ProductOption productOption = productOptionRepository.findById(orderProduct.getOrderProductOptionId())
                                .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
                        productOptionTitle = productOption.getProductOptionTitle();
                    }

                    return OrderProductDetailResponseDto.from(orderProduct, product.getProductTitle(), productOptionTitle);
                }).collect(Collectors.toList());

        return OrderDetailResponseDto.from(order, orderProductDetailResponseDto, aes256Encoder);
    }

    @Transactional
    public OrderCancelResponseDto updateOrderStatusCancel(Member member, Long orderId) {
        Order order = orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 없습니다."));

        // 취소 가능 상태인지 확인
        if (!order.getOrderStatus().isCancellable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "취소 가능 상태가 아닙니다.");
        }

        order.setOrderStatus(OrderStatusEnum.ORDER_CANCELLED);

        List<OrderProduct> orderProducts = orderProductRepository.findByOrderOrderId(orderId);
        for (OrderProduct orderProduct : orderProducts) {
            Product product = orderProduct.getProduct();
            product.setProductStock(product.getProductStock() + orderProduct.getOrderProductQuantity());
            productRepository.save(product);

            if (orderProduct.getOrderProductOptionId() != 0) {
                ProductOption productOption = productOptionRepository.findById(orderProduct.getOrderProductOptionId())
                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
                productOption.setProductOptionStock(productOption.getProductOptionStock() + orderProduct.getOrderProductQuantity());
                productOptionRepository.save(productOption);
            }
        }

        orderRepository.save(order);

        return OrderCancelResponseDto.from(order);
    }

    @Transactional
    public OrderReturnResponseDto updateOrderStatusReturn(Member member, Long orderId) {
        Order order = orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 없습니다."));

        // 반품 가능 상태인지 확인
        if (!order.getOrderStatus().isReturnable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반품 가능 상태가 아닙니다.");
        }

        // 반품 가능 기간인지 확인 (배송 완료 후 1일 이내)
        if (order.getUpdatedAt().plusMinutes(1).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반품 가능 기간이 아닙니다. (배송 완료 후 1일 이내)");
        }

        order.setOrderStatus(OrderStatusEnum.RETURN_REQUESTED);
        orderRepository.save(order);

        return OrderReturnResponseDto.from(order);
    }
}
