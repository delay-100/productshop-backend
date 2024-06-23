package com.whitedelay.productshop.member.controller;

import com.whitedelay.productshop.member.dto.MemberMyinfoRequestDto;
import com.whitedelay.productshop.member.dto.MemberMyinfoResponseDto;
import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.member.dto.SignupRequestDto;
import com.whitedelay.productshop.member.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    final private AuthService authService;

    @PostMapping("signup/form")
    public ApiResponse<?> signup(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {
       if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            System.out.println("Validation errors: " + errors);
            return ApiResponse.createFail(bindingResult);
        }

        return ApiResponse.createSuccess(authService.signup(requestDto));
    }

    @GetMapping("/member/myinfo")
    public ApiResponse<MemberMyinfoResponseDto> getMemberMyinfo(@CookieValue("${AUTHORIZATION_HEADER}") String userToken) {
        return ApiResponse.createSuccess(authService.getMemberMyinfo(userToken));
    }

    @PostMapping("/member/myinfo")
    public ApiResponse<MemberMyinfoResponseDto> updateMemberMyinfo(@CookieValue("${AUTHORIZATION_HEADER}") String userToken, @RequestBody MemberMyinfoRequestDto memberMyinfoRequestDto) {
        return ApiResponse.createSuccess(authService.updateMemberMyinfo(userToken, memberMyinfoRequestDto));
    }
}
