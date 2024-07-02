package com.whitedelay.productshop.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank
    private String memberId;
    @NotBlank
    private String password;
}