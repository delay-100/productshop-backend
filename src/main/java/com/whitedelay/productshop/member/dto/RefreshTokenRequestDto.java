package com.whitedelay.productshop.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequestDto {
    @NotBlank
    String memberId;
    @NotBlank
    String refreshToken;
}
