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
import com.whitedelay.productshop.util.AES256Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AES256Encoder aes256Encoder;

    private Member member;
    private Order order;
    private Product product;
    private ProductOption productOption;
    private OrderProduct orderProduct;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .memberId("testuser")
                .build();

        product = Product.builder()
                .productId(1L)
                .productTitle("샘플 상품")
                .productPrice(1000)
                .build();

        productOption = ProductOption.builder()
                .productOptionId(1L)
                .productOptionTitle("샘플 옵션")
                .productOptionPrice(100)
                .productOptionStock(10)
                .build();

        order = Order.builder()
                .orderId(1L)
                .member(member)
                .orderStatus(OrderStatusEnum.PAYMENT_COMPLETED)
                .build();

        orderProduct = OrderProduct.builder()
                .orderProductId(1L)
                .order(order)
                .product(product)
                .orderProductOptionId(productOption.getProductOptionId())
                .orderProductQuantity(2)
                .build();
    }


    @Test
    @DisplayName("주문 상품 정보 조회")
    void getOrderProductAllInfo_Success() {
        // Given
        OrderProductAllInfoRequestDto requestDto = OrderProductAllInfoRequestDto.builder()
                .orderProducts(Arrays.asList(
                        OrderProductInfoRequestDto.builder()
                                .productId(product.getProductId())
                                .productOptionId(productOption.getProductOptionId())
                                .quantity(2)
                                .build()
                ))
                .build();

        when(memberRepository.findByMemberId(member.getMemberId())).thenReturn(Optional.of(member));
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(productOptionRepository.findByProductOptionId(productOption.getProductOptionId())).thenReturn(Optional.of(productOption));

        // When
        OrderProductAllInfoResponseDto responseDto = orderService.getOrderProductAllInfo(member, requestDto);

        // Then
        assertAll(
                () -> assertThat(responseDto).isNotNull(),
                () -> assertThat(responseDto.getOrderProducts()).hasSize(1),
                () -> assertThat(responseDto.getProductTotalPrice()).isEqualTo((product.getProductPrice() + productOption.getProductOptionPrice()) * 2),
                () -> assertThat(responseDto.getOrderPrice()).isEqualTo((product.getProductPrice() + productOption.getProductOptionPrice()) * 2 + (responseDto.getProductTotalPrice() >= 30000 ? 0 : 3000))
        );
    }

//    @Test
//    @DisplayName("주문 결제")
//    void postOrderProductPay_Success() {
//        // Given
//        OrderProductPayRequestDto requestDto = OrderProductPayRequestDto.builder()
//                .orderProductList(Arrays.asList(
//                        OrderProductResponseDto.builder()
//                                .productId(product.getProductId())
//                                .productOptionId(productOption.getProductOptionId())
//                                .quantity(1) // 테스트 데이터에 맞는 수량으로 설정
//                                .productPrice(product.getProductPrice())
//                                .productOptionPrice(productOption.getProductOptionPrice())
//                                .productTotalPrice((product.getProductPrice() + productOption.getProductOptionPrice()))
//                                .build()
//                ))
//                .totalOrderPrice((product.getProductPrice() + productOption.getProductOptionPrice()))
//                .orderShippingFee(0)
//                .orderPrice((product.getProductPrice() + productOption.getProductOptionPrice()))
//                .build();
//
//        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
//        when(productOptionRepository.findById(productOption.getProductOptionId())).thenReturn(Optional.of(productOption));
//
//        // When
//        OrderProductPayResponseDto response = orderService.postOrderProductPay(member, requestDto);
//
//        // Then
//        assertAll(
//                () -> assertThat(response.getTotalOrderPrice()).isEqualTo(requestDto.getTotalOrderPrice()),
//                () -> assertThat(response.getOrderShippingFee()).isEqualTo(requestDto.getOrderShippingFee()),
//                () -> assertThat(response.getOrderPrice()).isEqualTo(requestDto.getOrderPrice())
//        );
//    }
//
//    @Test
//    @DisplayName("주문 상품 정보 조회 실패 - 상품 없음")
//    void getOrderProductAllInfo_Failure_ProductNotFound() {
//        // Given
//        OrderProductAllInfoRequestDto requestDto = OrderProductAllInfoRequestDto.builder()
//                .orderProducts(Arrays.asList(
//                        OrderProductInfoRequestDto.builder()
//                                .productId(999L) // 존재하지 않는 상품 ID
//                                .productOptionId(productOption.getProductOptionId())
//                                .quantity(2)
//                                .build()
//                ))
//                .build();
//
//        when(productRepository.findById(999L)).thenReturn(Optional.empty());
//
//        // When / Then
//        assertThatThrownBy(() -> orderService.getOrderProductAllInfo(member, requestDto))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("찾는 상품이 없습니다.");
//    }
//
//    @Test
//    @DisplayName("주문 결제 실패 - 재고 부족")
//    void postOrderProductPay_Failure_InsufficientStock() {
//        // Given
//        OrderProductPayRequestDto requestDto = OrderProductPayRequestDto.builder()
//                .orderProductList(Arrays.asList(
//                        OrderProductResponseDto.builder()
//                                .productId(product.getProductId())
//                                .productOptionId(productOption.getProductOptionId())
//                                .productPrice(product.getProductPrice())
//                                .productOptionPrice(productOption.getProductOptionPrice())
//                                .productTotalPrice((product.getProductPrice() + productOption.getProductOptionPrice()) * 20)
//                                .build()
//                ))
//                .totalOrderPrice((product.getProductPrice() + productOption.getProductOptionPrice()) * 20)
//                .orderShippingFee(0)
//                .orderPrice((product.getProductPrice() + productOption.getProductOptionPrice()) * 20)
//                .build();
//
//        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
//        when(productOptionRepository.findById(productOption.getProductOptionId())).thenReturn(Optional.of(productOption));
//
//        // When / Then
//        assertThrows(ResponseStatusException.class, () -> {
//            orderService.postOrderProductPay(member, requestDto);
//        });
//    }

    @Test
    @DisplayName("주문 목록 조회")
    void getOrderList_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(Collections.singletonList(order), pageable, 1);

        when(orderRepository.findByMemberMemberId(member.getMemberId(), pageable)).thenReturn(orderPage);
        when(orderProductRepository.findByOrderOrderId(order.getOrderId())).thenReturn(Collections.singletonList(orderProduct));
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        // When
        Page<OrderListResponseDto> response = orderService.getOrderList(member, 0, 10);

        // Then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getContent()).hasSize(1),
                () -> assertThat(response.getContent().get(0).getProductTitle()).isEqualTo(product.getProductTitle())
        );
    }

    @Test
    @DisplayName("주문 상세 조회")
    void getOrderDetail_Success() {
        // Given
        List<OrderProduct> orderProducts = Collections.singletonList(
                OrderProduct.builder()
                        .product(product)
                        .orderProductQuantity(1)
                        .orderProductOptionId(1L)
                        .orderProductOptionPrice(1000)
                        .orderProductPrice(500)
                        .build()
        );

        when(orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), order.getOrderId())).thenReturn(Optional.of(order));
        when(orderProductRepository.findByOrderOrderId(order.getOrderId())).thenReturn(orderProducts);
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(productOptionRepository.findProductOptionTitleById(anyLong())).thenReturn("샘플 옵션"); // Mock 설정 추가

        // When
        OrderDetailResponseDto responseDto = orderService.getOrderDetail(member, order.getOrderId());

        // Then
        assertAll(
                () -> assertThat(responseDto).isNotNull(),
                () -> assertThat(responseDto.getOrderId()).isEqualTo(order.getOrderId()),
                () -> assertThat(responseDto.getOrderProductDetailResponseDto()).hasSize(1),
                () -> assertThat(responseDto.getOrderProductDetailResponseDto().get(0).getProductTitle()).isEqualTo("샘플 상품"),
                () -> assertThat(responseDto.getOrderProductDetailResponseDto().get(0).getProductOptionTitle()).isEqualTo("샘플 옵션")
        );
    }


    @Test
    @DisplayName("주문 상세 조회 실패 - 주문이 존재하지 않음")
    void getOrderDetail_Failure_OrderNotFound() {
        // Given
        when(orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), order.getOrderId())).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> orderService.getOrderDetail(member, order.getOrderId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 주문이 없습니다.");
    }

    @Test
    @DisplayName("주문 취소")
    void updateOrderStatusCancel_Success() {
        // Given
        when(orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), order.getOrderId())).thenReturn(Optional.of(order));
        when(orderProductRepository.findByOrderOrderId(order.getOrderId())).thenReturn(Collections.singletonList(orderProduct));
        when(productOptionRepository.findById(productOption.getProductOptionId())).thenReturn(Optional.of(productOption));

        // When
        OrderCancelResponseDto response = orderService.updateOrderStatusCancel(member, order.getOrderId());

        // Then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getOrderStatus()).isEqualTo(OrderStatusEnum.ORDER_CANCELLED),
                () -> assertThat(productOption.getProductOptionStock()).isEqualTo(12)  // 원래 재고로 복원
        );

        // Verify that the order status update was called
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("주문 취소 실패 - 취소 가능 상태가 아님")
    void updateOrderStatusCancel_Failure_InvalidStatus() {
        // Given
        order.setOrderStatus(OrderStatusEnum.SHIPPING);

        when(orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), order.getOrderId())).thenReturn(Optional.of(order));

        // When / Then
        assertThatThrownBy(() -> orderService.updateOrderStatusCancel(member, order.getOrderId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("취소 가능 상태가 아닙니다.");
    }

    @Test
    @DisplayName("주문 취소 실패 - 주문이 존재하지 않음")
    void updateOrderStatusCancel_Failure_OrderNotFound() {
        // Given
        when(orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), order.getOrderId())).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> orderService.updateOrderStatusCancel(member, order.getOrderId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 주문이 없습니다.");
    }

    @Test
    @DisplayName("주문 반품")
    void updateOrderStatusReturn_Success() {
        // Given
        Order order = Order.builder()
                .orderId(1L)
                .member(member)
                .orderStatus(OrderStatusEnum.DELIVERY_COMPLETED) // 반품 가능한 상태로 설정
                .build();

        LocalDateTime mockUpdatedAt = LocalDateTime.now().minusHours(1);
        ReflectionTestUtils.setField(order, "updatedAt", mockUpdatedAt);

        when(orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), order.getOrderId()))
                .thenReturn(Optional.of(order));

        // When
        OrderReturnResponseDto response = orderService.updateOrderStatusReturn(member, order.getOrderId());

        // Then
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatusEnum.RETURN_REQUESTED);
    }

    @Test
    @DisplayName("주문 반품 실패 - 반품 가능 상태가 아님")
    void updateOrderStatusReturn_Failure_InvalidStatus() {
        // Given
        order.setOrderStatus(OrderStatusEnum.SHIPPING);

        when(orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), order.getOrderId())).thenReturn(Optional.of(order));

        // When / Then
        assertThatThrownBy(() -> orderService.updateOrderStatusReturn(member, order.getOrderId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("반품 가능 상태가 아닙니다.");
    }

    @Test
    @DisplayName("주문 반품 실패 - 반품 가능 기간 초과")
    void updateOrderStatusReturn_Failure_ExpiredPeriod() {
        // Given
        order.setOrderStatus(OrderStatusEnum.DELIVERY_COMPLETED);
        Order spyOrder = spy(order);
        LocalDateTime mockUpdatedAt = LocalDateTime.now().minusDays(2);
        doReturn(mockUpdatedAt).when(spyOrder).getUpdatedAt();

        when(orderRepository.findByMemberMemberIdAndOrderId(member.getMemberId(), spyOrder.getOrderId())).thenReturn(Optional.of(spyOrder));

        // When / Then
        assertThatThrownBy(() -> orderService.updateOrderStatusReturn(member, spyOrder.getOrderId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("반품 가능 기간이 아닙니다.");
    }
}
