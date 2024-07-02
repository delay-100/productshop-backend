package com.whitedelay.productshop.member.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponseDto {
    String memberId;
    String refreshToken;

    public static LoginResponseDto from(String memberId, String refreshToken) {
        return LoginResponseDto.builder()
                .memberId(memberId)
                .refreshToken(refreshToken)
                .build();
    }
}
