package com.whitedelay.productshop.mail.controller;

import com.whitedelay.productshop.mail.dto.SignupVerificationEmailDto;
import com.whitedelay.productshop.mail.dto.SignupVerifyCodeDto;
import com.whitedelay.productshop.mail.service.MailService;
import com.whitedelay.productshop.util.ApiResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    private static final String BASE_AUTH = "/auth";

    /**
     * 회원가입 이메일 전송
     * @param signupVerificationEmailDto 가입할 이메일 정보
     * @return 이메일 전송 성공 여부(T/F)
     */
    @PostMapping(BASE_AUTH + "/send-signup-code")
    public ApiResponse<Boolean> postSignupVerificationEmail(
            @RequestBody SignupVerificationEmailDto signupVerificationEmailDto
    ) throws MessagingException, UnsupportedEncodingException {
        return ApiResponse.createSuccess(mailService.postSignupVerificationEmail(signupVerificationEmailDto.getEmail()));
    }

    /**
     * 회원가입 이메일 코드 확인
     * @param signupVerifyCodeDto 인증할 정보
     * @return 이메일 코드 확인 여부(T/F)
     */
    @PostMapping(BASE_AUTH + "/check-signup-code")
    public ApiResponse<Boolean> checkSignupEmailCode(
            @RequestBody SignupVerifyCodeDto signupVerifyCodeDto
    ){
        return ApiResponse.createSuccess(mailService.checkSignupEmailCode(signupVerifyCodeDto.getEmail(), signupVerifyCodeDto.getEmailCode()));
    }
}
