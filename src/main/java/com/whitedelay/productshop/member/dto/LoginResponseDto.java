package com.whitedelay.productshop.member.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponseDto {
    String memberId;
    String refreshToken;
}
