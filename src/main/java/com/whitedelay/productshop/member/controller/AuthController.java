package com.whitedelay.productshop.member.controller;

import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.member.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    final private AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ApiResponse<?> signup(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {
//    public ApiResponse<?> signup(@RequestBody SignupRequestDto requestDto) {
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

    // 로그인
    @PostMapping("/member/login")
    public ApiResponse<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse res
    ) {
        return ApiResponse.createSuccess(authService.login(loginRequestDto, res));
    }

    @GetMapping("/member/myinfo")
    public ApiResponse<MemberMyinfoResponseDto> getMemberMyinfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
        return ApiResponse.createSuccess(authService.getMemberMyinfo(userDetails.getMember()));
    }

    @PatchMapping("/member/myinfo")
    public ApiResponse<MemberMyinfoResponseDto> updateMemberMyinfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails
            , @RequestBody MemberMyinfoRequestDto memberMyinfoRequestDto
    ) {
        return ApiResponse.createSuccess(authService.updateMemberMyinfo(userDetails.getMember(), memberMyinfoRequestDto));
    }

    @PatchMapping("/member/modify/password")
    public ApiResponse<Boolean> updateMemberPassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails
            , @RequestBody MemberpasswordRequestDto memberpasswordRequestDto
    ) {
        return ApiResponse.createSuccess(authService.updateMemberPassword(userDetails.getMember(), memberpasswordRequestDto));
    }

    // access토큰 만료 시 호출되는 메소드
//    public ApiResponse<ResponseTokenResponseDto> refreshToken (@RequestBody ) {
//
//    }
}
