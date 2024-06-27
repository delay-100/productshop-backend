package com.whitedelay.productshop.order.controller;

import com.whitedelay.productshop.order.dto.OrderProductAllInfoRequestDto;
import com.whitedelay.productshop.order.dto.OrderProductAllInfoResponseDto;
import com.whitedelay.productshop.order.dto.OrderProductPayRequestDto;
import com.whitedelay.productshop.order.dto.OrderProductPayResponseDto;
import com.whitedelay.productshop.order.service.OrderService;
import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 배송지 입력 및 결제확인 접근 - 구매하기 눌렀을때 나오는 info
    @GetMapping("/order/info")
    public ApiResponse<OrderProductAllInfoResponseDto> getOrderProductAllInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody OrderProductAllInfoRequestDto orderProductAllInfoRequestDto
    ) {
        return ApiResponse.createSuccess(orderService.getOrderProductAllInfo(userDetails.getMember(), orderProductAllInfoRequestDto));
    }

    // 상품 주문
    @PostMapping("/order/pay")
    public ApiResponse<OrderProductPayResponseDto> postOrderProductPay(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody OrderProductPayRequestDto orderProductPayRequestDto
    ) {
        return ApiResponse.createSuccess(orderService.postOrderProductPay(userDetails.getMember(), orderProductPayRequestDto));
    }

}
