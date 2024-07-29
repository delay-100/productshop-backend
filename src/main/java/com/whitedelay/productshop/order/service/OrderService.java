package com.whitedelay.productshop.order.service;

import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.order.dto.*;
import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.order.entity.OrderProduct;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import com.whitedelay.productshop.order.repository.OrderProductRepository;
import com.whitedelay.productshop.order.repository.OrderRepository;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import com.whitedelay.productshop.product.repository.ProductOptionRepository;
import com.whitedelay.productshop.product.repository.ProductRepository;
import com.whitedelay.productshop.redis.service.RedisService;
import com.whitedelay.productshop.util.AES256Encoder;
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

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final AES256Encoder aes256Encoder;
    private final RedisService redisService;
    private final OrderProductService orderProductService;

    @Transactional(readOnly = true)
    public OrderProductAllInfoResponseDto getOrderProductAllInfo(Member member, OrderProductAllInfoRequestDto orderProductAllInfoRequestDto) {
        member = memberRepository.findByMemberId(member.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));
        List<OrderProductResponseDto> orderProducts = orderProductAllInfoRequestDto.getOrderProducts().stream().map(orderProduct -> {
            Product product = productRepository.findById(orderProduct.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));

            int productPrice = product.getProductPrice();

            ProductOption productOption = productOptionRepository.findByProductOptionId(orderProduct.getProductOptionId())
                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
            int productOptionPrice = productOption.getProductOptionPrice();


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
    public OrderProductPayResponseDto createOrderProductPay(Member member, OrderProductPayRequestDto orderProductPayRequestDto) {
        List<DetuctedProductInfo> detuctedProductInfoList = new ArrayList<>();

        try {
            orderProductPayRequestDto.getOrderProductList().forEach(orderProduct -> {
                if (!redisService.deductStock(orderProduct.getProductId(), orderProduct.getProductOptionId(), orderProduct.getQuantity())) {
                    throw new IllegalArgumentException("상품 옵션의 재고가 부족합니다.");
                }
                detuctedProductInfoList.add(
                        DetuctedProductInfo.builder()
                                .productId(orderProduct.getProductId())
                                .productOptionId(orderProduct.getProductOptionId())
                                .productOptionStock(orderProduct.getQuantity())
                                .build()
                );
            });
            orderProductService.createOrderProductPay(member, orderProductPayRequestDto);
            return OrderProductPayResponseDto.from(
                    orderProductPayRequestDto.getProductTotalPrice(),
                    orderProductPayRequestDto.getOrderShippingFee(),
                    orderProductPayRequestDto.getOrderPrice(),
                    OrderStatusEnum.PAYMENT_COMPLETED
            );

        } catch (Exception e) {
            detuctedProductInfoList.forEach(stock -> redisService.deductRollbackStock(
                    stock.getProductId(),
                    stock.getProductOptionId(),
                    stock.getProductOptionStock()
            ));
            System.out.println("e = " + e);
            // 이전까지 차감했던 값 다시 증가시키기
            throw e;
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

            // 묶음 상품에 대해서 대표 상품에 대한 정보만 출력함
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

                    String productOptionTitle = productOptionRepository.findProductOptionTitleById(orderProduct.getOrderProductOptionId());

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
            ProductOption productOption = productOptionRepository.findById(orderProduct.getOrderProductOptionId())
                    .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
            productOption.setProductOptionStock(productOption.getProductOptionStock() + orderProduct.getOrderProductQuantity());
            productOptionRepository.save(productOption);
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
        if (order.getUpdatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반품 가능 기간이 아닙니다. (배송 완료 후 1일 이내)");
        }

        order.setOrderStatus(OrderStatusEnum.RETURN_REQUESTED);
        orderRepository.save(order);

        return OrderReturnResponseDto.from(order);
    }

}
