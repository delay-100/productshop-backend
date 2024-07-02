package com.whitedelay.productshop.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogoutRequestDto {
    @NotBlank
    private String memberId;
    @NotBlank
    private String password;
}