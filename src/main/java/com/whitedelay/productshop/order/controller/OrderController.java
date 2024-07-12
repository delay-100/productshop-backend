package com.whitedelay.productshop.order.controller;

import com.whitedelay.productshop.member.dto.OrderCancelResponseDto;
import com.whitedelay.productshop.member.dto.OrderDetailResponseDto;
import com.whitedelay.productshop.member.dto.OrderListResponseDto;
import com.whitedelay.productshop.member.dto.OrderReturnResponseDto;
import com.whitedelay.productshop.order.dto.OrderProductAllInfoRequestDto;
import com.whitedelay.productshop.order.dto.OrderProductAllInfoResponseDto;
import com.whitedelay.productshop.order.dto.OrderProductPayRequestDto;
import com.whitedelay.productshop.order.dto.OrderProductPayResponseDto;
import com.whitedelay.productshop.order.service.OrderService;
import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private static final String BASE_ORDER = "/order";
    private static final String BASE_MYPAGE = "/mypage";

    /**
     * GET
     * 배송지 입력 및 결제확인
     * @param userDetails security의 회원 정보
     * @param orderProductAllInfoRequestDto 주문 상품 요청 객체
     * @return 회원의 상품 주문 정보 객체 DTO
     */
    @GetMapping(BASE_ORDER + "/info")
    public ApiResponse<OrderProductAllInfoResponseDto> getOrderProductAllInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody OrderProductAllInfoRequestDto orderProductAllInfoRequestDto
        ) {
        return ApiResponse.createSuccess(orderService.getOrderProductAllInfo(userDetails.getMember(), orderProductAllInfoRequestDto));
    }

    /**
     * POST
     * 상품 주문
     * @param userDetails security의 회원 정보
     * @param orderProductPayRequestDto 주문 객체
     * @return 결제 성공/실패 결과 객체 DTO
     */
    @PostMapping(BASE_ORDER + "/pay")
    public ApiResponse<OrderProductPayResponseDto> createOrderProductPay(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody OrderProductPayRequestDto orderProductPayRequestDto
        ) {
        return ApiResponse.createSuccess(orderService.createOrderProductPay(userDetails.getMember(), orderProductPayRequestDto));
    }


    /**
     * GET
     * 주문 내역 리스트
     * @param userDetails security의 회원 정보
     * @param page 페이지 번호
     * @param size 한 페이지에 띄울 수
     * @return 주문 정보 리스트 DTO
     */
    @GetMapping(BASE_MYPAGE + "/orderlist")
    public ApiResponse<Page<OrderListResponseDto>> getOrderList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int page,
            @RequestParam int size
        ) {
        return ApiResponse.createSuccess(orderService.getOrderList(userDetails.getMember(), page, size));
    }

    /**
     * GET
     * 주문내역 상세
     * @param userDetails security의 회원 정보
     * @param orderId 주문 아이디
     * @return 하나의 주문 내역에 대한 정보 DTO
     */
    @GetMapping(BASE_MYPAGE + "/order")
    public ApiResponse<OrderDetailResponseDto> getOrderDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long orderId
        ) {
        return ApiResponse.createSuccess(orderService.getOrderDetail(userDetails.getMember(), orderId));
    }

    /**
     * GET
     * 한 주문 내 상품 전체 취소
     * @param userDetails security의 회원 정보
     * @param orderId 주문 아이디
     * @return 취소된 주문 내역에 대한 정보 DTO
     */
    @PatchMapping(BASE_MYPAGE + "/order/cancel")
    public ApiResponse<OrderCancelResponseDto> updateOrderStatusCancel(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long orderId
        ) {
        return ApiResponse.createSuccess(orderService.updateOrderStatusCancel(userDetails.getMember(), orderId));
    }

    /**
     * 한 주문 내 상품 전체 반품
     * @param userDetails security의 회원 정보
     * @param orderId 주문 아이디
     * @return 반품된 주문 내역에 대한 정보 DTO
     */
    @PatchMapping(BASE_MYPAGE + "/order/return")
    public ApiResponse<OrderReturnResponseDto> updateOrderStatusReturn(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long orderId
        ) {
        return ApiResponse.createSuccess(orderService.updateOrderStatusReturn(userDetails.getMember(), orderId));
    }
}
