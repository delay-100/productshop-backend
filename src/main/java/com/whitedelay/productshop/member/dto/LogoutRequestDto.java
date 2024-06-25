package com.whitedelay.productshop.member.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogoutRequestDto {
    private String memberId;
    private String password;
}