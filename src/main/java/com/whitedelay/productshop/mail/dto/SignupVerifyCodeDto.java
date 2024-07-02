package com.whitedelay.productshop.mail.dto;

import lombok.Getter;

@Getter
public class SignupVerifyCodeDto {
    private String emailCode;
    private String email;
}
