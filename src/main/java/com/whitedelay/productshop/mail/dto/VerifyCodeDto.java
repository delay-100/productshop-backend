package com.whitedelay.productshop.mail.dto;

import lombok.Getter;

@Getter
public class VerifyCodeDto {
    private String emailCode;
    private String email;
}
