package com.whitedelay.productshop.security.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDto {
    private String memberid;
    private String password;
}