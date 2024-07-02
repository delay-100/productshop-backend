package com.whitedelay.productshop.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberMyInfoRequestDto {
    @NotBlank
    private String address;
    @NotBlank
    private String phone;
}
