package com.whitedelay.productshop.member.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RefreshTokenResponseDto {
    String accessToken;
    String memberId;
}
