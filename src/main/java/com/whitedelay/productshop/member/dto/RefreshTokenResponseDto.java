package com.whitedelay.productshop.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class RefreshTokenResponseDto {

    @NotBlank
    String memberId;

    @NotBlank
    String accessToken;

    public static RefreshTokenResponseDto from(String memberId, String accessToken) {
        return RefreshTokenResponseDto.builder()
                .accessToken(accessToken)
                .memberId(memberId)
                .build();
    }
}
