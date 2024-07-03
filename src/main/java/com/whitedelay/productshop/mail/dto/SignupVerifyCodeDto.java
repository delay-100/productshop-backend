package com.whitedelay.productshop.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupVerifyCodeDto {
    private String emailCode;
    private String email;
}
