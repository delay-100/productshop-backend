package com.whitedelay.productshop.mail.controller;

import com.whitedelay.productshop.mail.dto.VerifyCodeDto;
import com.whitedelay.productshop.mail.service.MailService;
import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.mail.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
    private final RedisService redisService;

    @PostMapping("signup/send-verification-code")
    public ApiResponse<Boolean> sendEmail(@RequestParam(value="email") String email) throws Exception {
        String code = mailService.sendSimpleMessage(email);

        return ApiResponse.createSuccess(redisService.saveVerificationCode(email, code));
    }

    @PostMapping("/signup/check-verification-code")
    public ApiResponse<Boolean> checkEmailCode(@RequestBody VerifyCodeDto verifyCodeDto){
        return ApiResponse.createSuccess(redisService.verifyEmail(verifyCodeDto.getEmail(), verifyCodeDto.getEmailCode()));
    }
}
