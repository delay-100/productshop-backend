package com.whitedelay.productshop.member.controller;

import com.whitedelay.productshop.member.dto.TempDto;
import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.member.dto.SignupRequestDto;
import com.whitedelay.productshop.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    final private MemberService memberService;

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

        return ApiResponse.createSuccess(memberService.signup(requestDto));
    }

    // 복호화 확인용 - 임시 유저 확인
    @GetMapping("/temp/user")
    public ApiResponse<SignupRequestDto> tempCheckUser(@RequestBody TempDto tempDto) {
        System.out.println("id= "+tempDto.getMemberid());
        System.out.println("password = " + tempDto.getPassword());
        return ApiResponse.createSuccess(memberService.tempCheckUser(tempDto.getMemberid(), tempDto.getPassword()));
    }
}
