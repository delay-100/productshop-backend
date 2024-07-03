package com.whitedelay.productshop.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenRequestDto {
    @NotBlank
    String memberId;
    @NotBlank
    String refreshToken;
}
