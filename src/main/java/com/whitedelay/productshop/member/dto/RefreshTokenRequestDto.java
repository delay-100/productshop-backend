package com.whitedelay.productshop.member.dto;

import lombok.Getter;

@Getter
public class RefreshTokenRequestDto {
    String memberId;
    String refreshToken;
}
