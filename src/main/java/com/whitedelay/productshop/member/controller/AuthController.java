package com.whitedelay.productshop.member.controller;

import com.whitedelay.productshop.member.dto.LogoutRequestDto;
import com.whitedelay.productshop.member.dto.TempDto;
import com.whitedelay.productshop.security.jwt.JwtUtil;
import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.member.dto.SignupRequestDto;
import com.whitedelay.productshop.member.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

//    @PostMapping("logout")
//    public ApiResponse<Boolean> logout(@CookieValue("${REFRESHTOKEN_HEADER}") String refreshToken, HttpServletResponse response) {
//        return ApiResponse.createSuccess(authService.logout(refreshToken, response));
//    }

    // 복호화 확인용 - 임시 유저 확인
    @GetMapping("/temp/user")
    public ApiResponse<SignupRequestDto> tempCheckUser(@RequestBody TempDto tempDto) {
        System.out.println("id= "+tempDto.getMemberid());
        System.out.println("password = " + tempDto.getPassword());
        return ApiResponse.createSuccess(authService.tempCheckUser(tempDto.getMemberid(), tempDto.getPassword()));
    }
}
