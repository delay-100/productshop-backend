package com.whitedelay.productshop.member.mail.dto;

import lombok.Getter;

@Getter
public class VerifyCodeDto {
    private String emailCode;
    private String email;
}
