package com.whitedelay.productshop.order.controller;

import com.whitedelay.productshop.member.dto.OrderCancelResponseDto;
import com.whitedelay.productshop.member.dto.OrderDetailResponseDto;
import com.whitedelay.productshop.member.dto.OrderListResponseDto;
import com.whitedelay.productshop.member.dto.OrderReturnResponseDto;
import com.whitedelay.productshop.order.dto.OrderProductAllInfoRequestDto;
import com.whitedelay.productshop.order.dto.OrderProductAllInfoResponseDto;
import com.whitedelay.productshop.order.dto.OrderProductPayRequestDto;
import com.whitedelay.productshop.order.dto.OrderProductPayResponseDto;
import com.whitedelay.productshop.order.entity.OrderCardCompanyEnum;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import com.whitedelay.productshop.order.service.OrderService;
import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Mock
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        when(userDetails.getMember()).thenReturn(null); // 테스트용 사용자 정보 설정
    }

    @Test
    @DisplayName("주문 상품 정보 조회")
    void getOrderProductAllInfo_Success() {
        // Given
        OrderProductAllInfoRequestDto orderProductAllInfoRequestDto = OrderProductAllInfoRequestDto.builder()
                .orderProducts(Collections.emptyList())
                .build();

        OrderProductAllInfoResponseDto orderProductAllInfoResponseDto = OrderProductAllInfoResponseDto.builder()
                .orderMemberName("testUser")
                .orderZipCode(12345)
                .orderAddress("서울시 강남구 테헤란로 123")
                .orderPhone("010-1234-5678")
                .productTotalPrice(10000)
                .orderShippingFee(3000)
                .orderPrice(13000)
                .build();

        when(orderService.getOrderProductAllInfo(any(), any(OrderProductAllInfoRequestDto.class)))
                .thenReturn(orderProductAllInfoResponseDto);

        // When
        ApiResponse<OrderProductAllInfoResponseDto> response = orderController.getOrderProductAllInfo(userDetails, orderProductAllInfoRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");

        OrderProductAllInfoResponseDto dto = response.getData();
        assertThat(dto.getOrderMemberName()).isEqualTo("testUser");
        assertThat(dto.getOrderZipCode()).isEqualTo(12345);
        assertThat(dto.getOrderAddress()).isEqualTo("서울시 강남구 테헤란로 123");
        assertThat(dto.getOrderPhone()).isEqualTo("010-1234-5678");
        assertThat(dto.getProductTotalPrice()).isEqualTo(10000);
        assertThat(dto.getOrderShippingFee()).isEqualTo(3000);
        assertThat(dto.getOrderPrice()).isEqualTo(13000);
    }

    @Test
    @DisplayName("상품 주문")
    void postOrderProductPay_Success() {
        // Given
        OrderProductPayRequestDto orderProductPayRequestDto = OrderProductPayRequestDto.builder()
                .orderProducts(Collections.emptyList())
                .orderMemberName("testUser")
                .orderZipCode(12345)
                .orderAddress("서울시 강남구 테헤란로 123")
                .orderPhone("010-1234-5678")
                .orderCardCompany(OrderCardCompanyEnum.KAKAOBANK)
                .totalOrderPrice(10000)
                .orderShippingFee(3000)
                .orderPrice(13000)
                .build();

        OrderProductPayResponseDto orderProductPayResponseDto = OrderProductPayResponseDto.builder()
                .totalOrderPrice(10000)
                .orderShippingFee(3000)
                .orderPrice(13000)
                .paymentStatus(OrderStatusEnum.PAYMENT_COMPLETED)
                .build();

        when(orderService.postOrderProductPay(any(), any(OrderProductPayRequestDto.class)))
                .thenReturn(orderProductPayResponseDto);

        // When
        ApiResponse<OrderProductPayResponseDto> response = orderController.postOrderProductPay(userDetails, orderProductPayRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");

        OrderProductPayResponseDto dto = response.getData();
        assertThat(dto.getTotalOrderPrice()).isEqualTo(10000);
        assertThat(dto.getOrderShippingFee()).isEqualTo(3000);
        assertThat(dto.getOrderPrice()).isEqualTo(13000);
        assertThat(dto.getPaymentStatus()).isEqualTo(OrderStatusEnum.PAYMENT_COMPLETED);
    }

    @Test
    @DisplayName("주문 내역 리스트 조회")
    void getOrderList_Success() {
        // Given
        OrderListResponseDto orderListResponseDto1 = OrderListResponseDto.builder()
                .orderId(1L)
                .productTitle("샘플 상품1")
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatusEnum.PAYMENT_COMPLETED)
                .orderPrice(10000)
                .orderProductCount(2)
                .build();
        OrderListResponseDto orderListResponseDto2 = OrderListResponseDto.builder()
                .orderId(2L)
                .productTitle("샘플 상품2")
                .orderDate(LocalDateTime.now().minusDays(1))
                .orderStatus(OrderStatusEnum.SHIPPING)
                .orderPrice(20000)
                .orderProductCount(1)
                .build();
        Page<OrderListResponseDto> orderListResponseDtoPage = new PageImpl<>(
                Arrays.asList(orderListResponseDto1, orderListResponseDto2),
                PageRequest.of(0, 10), 2
        );

        when(orderService.getOrderList(any(), any(int.class), any(int.class))).thenReturn(orderListResponseDtoPage);

        // When
        ApiResponse<Page<OrderListResponseDto>> response = orderController.getOrderList(userDetails, 0, 10);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");

        Page<OrderListResponseDto> page = response.getData();
        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(2);

        OrderListResponseDto dto1 = page.getContent().get(0);
        assertThat(dto1.getOrderId()).isEqualTo(1L);
        assertThat(dto1.getProductTitle()).isEqualTo("샘플 상품1");
        assertThat(dto1.getOrderDate()).isNotNull();
        assertThat(dto1.getOrderStatus()).isEqualTo(OrderStatusEnum.PAYMENT_COMPLETED);
        assertThat(dto1.getOrderPrice()).isEqualTo(10000);
        assertThat(dto1.getOrderProductCount()).isEqualTo(2);

        OrderListResponseDto dto2 = page.getContent().get(1);
        assertThat(dto2.getOrderId()).isEqualTo(2L);
        assertThat(dto2.getProductTitle()).isEqualTo("샘플 상품2");
        assertThat(dto2.getOrderDate()).isNotNull();
        assertThat(dto2.getOrderStatus()).isEqualTo(OrderStatusEnum.SHIPPING);
        assertThat(dto2.getOrderPrice()).isEqualTo(20000);
        assertThat(dto2.getOrderProductCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("주문 내역 상세 조회")
    void getOrderDetail_Success() {
        // Given
        OrderDetailResponseDto orderDetailResponseDto = OrderDetailResponseDto.builder()
                .orderId(1L)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatusEnum.PAYMENT_COMPLETED)
                .orderShippingFee(3000)
                .orderPrice(13000)
                .orderMemberName("testUser")
                .orderZipCode(12345)
                .orderAddress("서울시 강남구 테헤란로 123")
                .orderPhone("010-1234-5678")
                .orderReq("빠른 배송 부탁드립니다.")
                .build();

        when(orderService.getOrderDetail(any(), any(Long.class)))
                .thenReturn(orderDetailResponseDto);

        // When
        ApiResponse<OrderDetailResponseDto> response = orderController.getOrderDetail(userDetails, 1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");

        OrderDetailResponseDto dto = response.getData();
        assertThat(dto.getOrderId()).isEqualTo(1L);
        assertThat(dto.getOrderMemberName()).isEqualTo("testUser");
        assertThat(dto.getOrderZipCode()).isEqualTo(12345);
        assertThat(dto.getOrderAddress()).isEqualTo("서울시 강남구 테헤란로 123");
        assertThat(dto.getOrderPhone()).isEqualTo("010-1234-5678");
        assertThat(dto.getOrderReq()).isEqualTo("빠른 배송 부탁드립니다.");
    }

    @Test
    @DisplayName("주문 취소")
    void updateOrderStatusCancel_Success() {
        // Given
        OrderCancelResponseDto orderCancelResponseDto = OrderCancelResponseDto.builder()
                .orderId(1L)
                .orderStatus(OrderStatusEnum.ORDER_CANCELLED)
                .build();

        when(orderService.updateOrderStatusCancel(any(), any(Long.class)))
                .thenReturn(orderCancelResponseDto);

        // When
        ApiResponse<OrderCancelResponseDto> response = orderController.updateOrderStatusCancel(userDetails, 1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");

        OrderCancelResponseDto dto = response.getData();
        assertThat(dto.getOrderId()).isEqualTo(1L);
        assertThat(dto.getOrderStatus()).isEqualTo(OrderStatusEnum.ORDER_CANCELLED);
    }

    @Test
    @DisplayName("주문 반품")
    void updateOrderStatusReturn_Success() {
        // Given
        OrderReturnResponseDto orderReturnResponseDto = OrderReturnResponseDto.builder()
                .orderId(1L)
                .orderStatus(OrderStatusEnum.RETURN_COMPLETED)
                .build();

        when(orderService.updateOrderStatusReturn(any(), any(Long.class)))
                .thenReturn(orderReturnResponseDto);

        // When
        ApiResponse<OrderReturnResponseDto> response = orderController.updateOrderStatusReturn(userDetails, 1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");

        OrderReturnResponseDto dto = response.getData();
        assertThat(dto.getOrderId()).isEqualTo(1L);
        assertThat(dto.getOrderStatus()).isEqualTo(OrderStatusEnum.RETURN_COMPLETED);
    }
}
