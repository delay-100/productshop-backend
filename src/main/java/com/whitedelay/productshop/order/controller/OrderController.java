package com.whitedelay.productshop.order.controller;

import com.whitedelay.productshop.order.dto.OrderProductAllInfoRequestDto;
import com.whitedelay.productshop.order.dto.OrderProductAllInfoResponseDto;
import com.whitedelay.productshop.order.service.OrderService;
import com.whitedelay.productshop.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 구매하기 눌렀을때 나오는 info
    @GetMapping("/order/info")
    public ApiResponse<OrderProductAllInfoResponseDto> getOrderProductAllInfo(
            @CookieValue("${AUTHORIZATION_HEADER}") String userToken,
            @RequestBody OrderProductAllInfoRequestDto orderProductAllInfoRequestDto
    ) {
        return ApiResponse.createSuccess(orderService.getOrderProductAllInfo(userToken, orderProductAllInfoRequestDto));
    }

}
