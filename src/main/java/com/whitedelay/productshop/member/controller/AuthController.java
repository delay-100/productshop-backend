package com.whitedelay.productshop.member.controller;

import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.member.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private static final String BASE_AUTH = "/auth"; // 누구나 접근 가능

    /**
     * POST
     * 회원가입
     * @param signupRequestDto 회원가입 요청 DTO
     * @return 회원가입 성공 여부(T/F)
     */
    @PostMapping(BASE_AUTH + "/signup")
    public ApiResponse<Boolean> signup(
            @RequestBody SignupRequestDto signupRequestDto
    ) {
        return ApiResponse.createSuccess(authService.signup(signupRequestDto));
    }

    /**
     * POST
     * 로그인
     * @param loginRequestDto 로그인 요청 DTO
     * @param res 서블릿 응답 객체
     * @return 로그인 응답 DTO
     */
    @PostMapping(BASE_AUTH + "/login")
    public ApiResponse<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse res
    ) {
        return ApiResponse.createSuccess(authService.login(loginRequestDto, res));
    }

    /**
     * POST
     * RefreshToken 재발급
     * 유효하지 않거나, 만료된 AccessToken에 대해 요청
     * @param refreshTokenRequestDto RefreshToken 요청 DTO
     * @param res 서블릿 응답 객체
     * @return RefreshToken 응답 DTO
     */
    @PostMapping("/refreshtoken")
    public ApiResponse<RefreshTokenResponseDto> refreshToken (
            @RequestBody RefreshTokenRequestDto refreshTokenRequestDto,
            HttpServletResponse res
    ) {
        return ApiResponse.createSuccess(authService.refreshToken(refreshTokenRequestDto.getMemberId(), refreshTokenRequestDto.getRefreshToken(), res));
    }
}
