package com.whitedelay.productshop.member.controller;

import com.whitedelay.productshop.member.dto.OrderCancelResponseDto;
import com.whitedelay.productshop.member.dto.OrderDetailResponseDto;
import com.whitedelay.productshop.member.dto.OrderListResponseDto;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.service.MemberService;
import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 주문 내역 리스트 확인
    @GetMapping("/mypage/orderlist")
    public ApiResponse<Page<OrderListResponseDto>> getOrderList(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam int page,
        @RequestParam int size
    ) {
        return ApiResponse.createSuccess(memberService.getOrderList(userDetails.getMember(), page, size));
    }

    // 주문 내역 상세 확인
    @GetMapping("/mypage/order")
    public ApiResponse<OrderDetailResponseDto> getOrderDetail(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam long orderId
    ) {
        return ApiResponse.createSuccess(memberService.getOrderDetail(userDetails.getMember(), orderId));
    }

    // 상품 취소
    @PatchMapping("/mypage/order/cancel")
    public ApiResponse<OrderCancelResponseDto> updateOrderStatusCancel(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam long orderId
    ) {
        return ApiResponse.createSuccess(memberService.updateOrderStatusCancel(userDetails.getMember(), orderId));
    }

}
