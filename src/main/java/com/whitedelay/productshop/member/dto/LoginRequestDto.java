package com.whitedelay.productshop.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class LoginRequestDto {
    private String memberId;
    private String password;
}