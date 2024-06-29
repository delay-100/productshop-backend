package com.whitedelay.productshop.member.controller;

import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.member.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
            @RequestBody LoginRequestDto loginRequestDto
            , HttpServletResponse res
    ) {
        return ApiResponse.createSuccess(authService.login(loginRequestDto, res));
    }

    // access토큰 만료 시 호출되는 메소드
    // 유효하지 않거나, 만료된 AccessToken에 대해 요청이 들어옴
    @PostMapping("/refreshtoken")
    public ApiResponse<RefreshTokenResponseDto> refreshToken (
            @RequestBody RefreshTokenRequestDto refreshTokenRequestDto
            , HttpServletResponse res
        ) {
        return ApiResponse.createSuccess(authService.refreshToken(refreshTokenRequestDto.getMemberId(), refreshTokenRequestDto.getRefreshToken(), res));
    }

    // 로그아웃 - redis의 토큰 삭제 & UserCookie비워주기
    @DeleteMapping("member/logout")
    public ApiResponse<Boolean> logout(
            @AuthenticationPrincipal UserDetailsImpl userDetails
            , HttpServletResponse res
    ) {
        return ApiResponse.createSuccess(authService.logout(userDetails.getMember(), res));
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


}
