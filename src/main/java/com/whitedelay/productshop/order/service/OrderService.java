package com.whitedelay.productshop.order.service;

import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.member.entity.Member;
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

            int productPrice = product.getProductPrice();
            int productOptionPrice = 0;
            ProductOption productOption = null;

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
    public OrderProductPayResponseDto postOrderProductPay(Member member, OrderProductPayRequestDto orderProductPayRequestDto) {
        Order order = Order.from(OrderRequestDto.from(orderProductPayRequestDto, OrderStatusEnum.PAYING, aes256Encoder, member));
        orderRepository.save(order);

        List<OrderProduct> orderProductList = new ArrayList<>(); // orderProducts 변수 선언
        Map<OrderProduct, Integer> originalProductStocks = new HashMap<>(); // 원래 product 재고를 기록할 맵
        Map<OrderProduct, Integer> originalOptionStocks = new HashMap<>(); // 원래 product option 재고를 기록할 맵

        for (OrderProductResponseDto orderProductDto : orderProductPayRequestDto.getOrderProducts()) {
            Product product = productRepository.findById(orderProductDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));

            ProductOption productOption = null;
            if (orderProductDto.getProductOptionId() != null) {
                productOption = productOptionRepository.findById(orderProductDto.getProductOptionId())
                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
            }

            orderProductList.add(OrderProduct.from(OrderProductRequestDto.from(order, product, orderProductDto.getQuantity(), productOption)));
        }

        orderProductRepository.saveAll(orderProductList); // OrderProduct 엔티티를 데이터베이스에 저장

        try {
            orderProductList.forEach(orderProduct -> {
                Product product = orderProduct.getProduct();
                originalProductStocks.put(orderProduct, product.getProductStock()); // 원래 product 재고 기록

                // 재고 부족 검사
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

                    product.setProductStock(product.getProductStock() - orderProduct.getOrderProductQuantity());
                    productRepository.save(product);
                }
            });

            // 결제 성공 시 주문 상태 업데이트
            order.setOrderStatus(OrderStatusEnum.PAYMENT_COMPLETED); // 주문 상태를 결제 완료로 업데이트
            order.setOrderPayYN(true); // 결제 완료 여부를 true로 설정
            orderRepository.save(order); // 업데이트된 주문 정보를 데이터베이스에 저장

            return OrderProductPayResponseDto.from(
                    orderProductPayRequestDto.getTotalOrderPrice(),
                    orderProductPayRequestDto.getOrderShippingFee(),
                    orderProductPayRequestDto.getOrderPrice(),
                    OrderStatusEnum.PAYMENT_COMPLETED
            );

        } catch (Exception e) {
            // 결제 실패 시 주문 상태 업데이트 및 재고 원상 복구
            orderProductList.forEach(orderProduct -> {
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
            });

            order.setOrderStatus(OrderStatusEnum.PAYMENT_FAILED); // 주문 상태를 결제 실패로 업데이트
            orderRepository.save(order); // 업데이트된 주문 정보를 데이터베이스에 저장

            return OrderProductPayResponseDto.from(
                    orderProductPayRequestDto.getTotalOrderPrice(),
                    orderProductPayRequestDto.getOrderShippingFee(),
                    orderProductPayRequestDto.getOrderPrice(),
                    OrderStatusEnum.PAYMENT_FAILED
                    );
        }
    }

//    // 값 Map으로 캐싱 -> 동시성 문제 발생..
//    @Transactional
//    public OrderProductPayResponseDto postOrderProductPay(Member member, OrderProductPayRequestDto orderProductPayRequestDto) {
//        Order order = Order.from(OrderRequestDto.from(orderProductPayRequestDto, OrderStatusEnum.PAYING, aes256Encoder, member));
//        orderRepository.save(order);
//
//        List<OrderProduct> orderProductList = new ArrayList<>();
//        Map<OrderProduct, Integer> originalProductStocks = new HashMap<>();
//        Map<OrderProduct, Integer> originalOptionStocks = new HashMap<>();
//        Map<Long, Product> productCache = new HashMap<>();
//        Map<Long, ProductOption> productOptionCache = new HashMap<>();
//
//        // 미리 필요한 모든 제품과 옵션 로드
//        for (OrderProductResponseDto orderProductDto : orderProductPayRequestDto.getOrderProducts()) {
//            Product product = productCache.computeIfAbsent(orderProductDto.getProductId(), id ->
//                    productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."))
//            );
//
//            ProductOption productOption = null;
//            if (orderProductDto.getProductOptionId() != null) {
//                productOption = productOptionCache.computeIfAbsent(orderProductDto.getProductOptionId(), id ->
//                        productOptionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."))
//                );
//            }
//
//            orderProductList.add(OrderProduct.from(OrderProductRequestDto.from(order, product, orderProductDto.getQuantity(), productOption)));
//        }
//
//        orderProductRepository.saveAll(orderProductList);
//
//        try {
//            updateStocks(orderProductList, originalProductStocks, originalOptionStocks, productOptionCache, productCache);
//
//            order.setOrderStatus(OrderStatusEnum.PAYMENT_COMPLETED);
//            order.setOrderPayYN(true);
//            orderRepository.save(order);
//
//            return OrderProductPayResponseDto.from(
//                    orderProductPayRequestDto.getTotalOrderPrice(),
//                    orderProductPayRequestDto.getOrderShippingFee(),
//                    orderProductPayRequestDto.getOrderPrice(),
//                    OrderStatusEnum.PAYMENT_COMPLETED
//            );
//
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        } catch (Exception e) {
//            restoreStocks(orderProductList, originalProductStocks, originalOptionStocks, productOptionCache, productCache);
//            order.setOrderStatus(OrderStatusEnum.PAYMENT_FAILED);
//            orderRepository.save(order);
//
//            return OrderProductPayResponseDto.from(
//                    orderProductPayRequestDto.getTotalOrderPrice(),
//                    orderProductPayRequestDto.getOrderShippingFee(),
//                    orderProductPayRequestDto.getOrderPrice(),
//                    OrderStatusEnum.PAYMENT_FAILED
//            );
//        }
//    }
//
//    private void updateStocks(List<OrderProduct> orderProductList, Map<OrderProduct, Integer> originalProductStocks, Map<OrderProduct, Integer> originalOptionStocks, Map<Long, ProductOption> productOptionCache, Map<Long, Product> productCache) {
//        orderProductList.forEach(orderProduct -> {
//            Product product = orderProduct.getProduct();
//            originalProductStocks.put(orderProduct, product.getProductStock());
//
//            if (orderProduct.getOrderProductOptionId() != 0) {
//                ProductOption productOption = productOptionCache.get(orderProduct.getOrderProductOptionId());
//                originalOptionStocks.put(orderProduct, productOption.getProductOptionStock());
//
//                if (productOption.getProductOptionStock() < orderProduct.getOrderProductQuantity()) {
//                    throw new IllegalArgumentException("상품 옵션의 재고가 부족합니다.");
//                }
//
//                productOption.setProductOptionStock(productOption.getProductOptionStock() - orderProduct.getOrderProductQuantity());
//            } else {
//                if (product.getProductStock() < orderProduct.getOrderProductQuantity()) {
//                    throw new IllegalArgumentException("상품의 재고가 부족합니다.");
//                }
//
//                product.setProductStock(product.getProductStock() - orderProduct.getOrderProductQuantity());
//            }
//        });
//
//        // 변경된 데이터를 일괄 저장
//        productRepository.saveAll(productCache.values());
//        productOptionRepository.saveAll(productOptionCache.values());
//    }
//
//
//    private void restoreStocks(List<OrderProduct> orderProductList, Map<OrderProduct, Integer> originalProductStocks, Map<OrderProduct, Integer> originalOptionStocks, Map<Long, ProductOption> productOptionCache, Map<Long, Product> productCache) {
//        orderProductList.forEach(orderProduct -> {
//            Product product = orderProduct.getProduct();
//            if (originalProductStocks.containsKey(orderProduct)) {
//                product.setProductStock(originalProductStocks.get(orderProduct));
//            }
//
//            if (orderProduct.getOrderProductOptionId() != 0) {
//                ProductOption productOption = productOptionCache.get(orderProduct.getOrderProductOptionId());
//                if (originalOptionStocks.containsKey(orderProduct)) {
//                    productOption.setProductOptionStock(originalOptionStocks.get(orderProduct));
//                }
//            }
//        });
//
//        // 변경된 데이터를 일괄 저장
//        productRepository.saveAll(productCache.values());
//        productOptionRepository.saveAll(productOptionCache.values());
//    }


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
        if (order.getUpdatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반품 가능 기간이 아닙니다. (배송 완료 후 1일 이내)");
        }

        order.setOrderStatus(OrderStatusEnum.RETURN_REQUESTED);
        orderRepository.save(order);

        return OrderReturnResponseDto.from(order);
    }

}
