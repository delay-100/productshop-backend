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

//    @Transactional
//    public OrderProductPayResponseDto postOrderProductPay(Member member, OrderProductPayRequestDto orderProductPayRequestDto) {
//        try {
////            1. List, Map의 형태에서 단위 item을 기준으로 주문 생성, 재고 차감, 결제하는 메소드 구현
////            2. 재고 차감만 row level lock이 적용되도록 구현(해결코드 1의 pessimistic write를 이용)
////            트랜잭션을 걸어두고, 락걸기 ...!!!
//
//            // 요청의 상품 리스트에 대해 productOptionId, productId 가 오름차순 정렬되어있다고 가정해야함. => 데드락 방지
//            // 아니면 여기서 정렬을 해야함,,! 정렬이 안되어있으면 아래와 같은 예외 발생
//            // ex) productId가 3 -> 5, 5 -> 3인 요청이 동시에 들어온 경우 데드락 발생.
//
//            List<OrderProduct> orderProductList = new ArrayList<>();
//            orderProductPayRequestDto.getOrderProductList().forEach(orderProduct -> {
//                if (!redisService.deductStock(orderProduct.getProductOptionId(), orderProduct.getQuantity())) {
//                    throw new IllegalArgumentException("상품 옵션의 재고가 부족합니다.");
//                }
//                // 트랜잭션이 끝나기 전까지 productId(+ productOptionId)에 대한 점유를 얘가 하고 있어야 함
//                ProductOption productOption = productOptionRepository.findByIdForUpdate(orderProduct.getProductOptionId())
//                            .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
//                if (productOption.getProductOptionStock() < orderProduct.getQuantity()) {
//                    throw new IllegalArgumentException("상품 옵션의 재고가 부족합니다.");
//                }
//                // 상품 옵션 재고 변경
////                    productOption.setProductOptionStock(productOption.getProductOptionStock() - orderProduct.getQuantity());
//                // 상품 옵션 재고 변경 쿼리 실행
//                productOptionRepository.updateStock(orderProduct.getProductOptionId(), productOption.getProductOptionStock() - orderProduct.getQuantity());
//
////                Product product = productRepository.findByIdForUpdate(orderProduct.getProductId())
////                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));
//                Product product = productRepository.findByProductId(orderProduct.getProductId())
//                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));
//                // 상품 재고 변경
////                product.setProductStock(product.getProductStock() - orderProduct.getQuantity());
//                orderProductList.add(OrderProduct.from(OrderProductRequestDto.from(product, orderProduct.getQuantity(), productOption)));
//            });
//
//            Order order = Order.from(OrderRequestDto.from(orderProductPayRequestDto, OrderStatusEnum.PAYMENT_COMPLETED, aes256Encoder, member));
//            orderRepository.save(order);
//            orderProductList.forEach(orderProduct -> orderProduct.setOrder(order));
//
//            orderProductRepository.saveAll(orderProductList);
//
//            return OrderProductPayResponseDto.from(
//                    orderProductPayRequestDto.getTotalOrderPrice(),
//                    orderProductPayRequestDto.getOrderShippingFee(),
//                    orderProductPayRequestDto.getOrderPrice(),
//                    OrderStatusEnum.PAYMENT_COMPLETED
//            );
//
//        } catch (Exception e) {
//            System.out.println("e = " + e);
////            orderRepository.save(Order.from(OrderRequestDto.from(orderProductPayRequestDto, OrderStatusEnum.PAYMENT_FAILED, aes256Encoder, member)));
////            return OrderProductPayResponseDto.from(
////                        orderProductPayRequestDto.getTotalOrderPrice(),
////                        orderProductPayRequestDto.getOrderShippingFee(),
////                        orderProductPayRequestDto.getOrderPrice(),
////                        OrderStatusEnum.PAYMENT_FAILED
////            );
////            throw new IllegalArgumentException("상품 주문 실패");
//            throw e;
//        }
//    }

    @Transactional
    public OrderProductPayResponseDto postOrderProductPay(Member member, OrderProductPayRequestDto orderProductPayRequestDto) {
        try {
            orderProductPayRequestDto.getOrderProductList().forEach(orderProduct -> {
                if (!redisService.deductStock(orderProduct.getProductOptionId(), orderProduct.getQuantity())) {
                    throw new IllegalArgumentException("상품 옵션의 재고가 부족합니다.");
                }
            });
            orderProductService.postOrderProductPay(member, orderProductPayRequestDto);
            return OrderProductPayResponseDto.from(
                    orderProductPayRequestDto.getTotalOrderPrice(),
                    orderProductPayRequestDto.getOrderShippingFee(),
                    orderProductPayRequestDto.getOrderPrice(),
                    OrderStatusEnum.PAYMENT_COMPLETED
            );

        } catch (Exception e) {
            System.out.println("e = " + e);
//            orderRepository.save(Order.from(OrderRequestDto.from(orderProductPayRequestDto, OrderStatusEnum.PAYMENT_FAILED, aes256Encoder, member)));
//            return OrderProductPayResponseDto.from(
//                        orderProductPayRequestDto.getTotalOrderPrice(),
//                        orderProductPayRequestDto.getOrderShippingFee(),
//                        orderProductPayRequestDto.getOrderPrice(),
//                        OrderStatusEnum.PAYMENT_FAILED
//            );
//            throw new IllegalArgumentException("상품 주문 실패");
            throw e;
        }
    }
    // order만들고
    // order save
    // cart에서 결제목록 가져오고
    // cart에서 가져온 결제목록을 orderdetails를 만드는데, 만드는 과정에서 재고안맞으면 exception발생


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
