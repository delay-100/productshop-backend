package com.whitedelay.productshop.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberPasswordRequestDto {
    @NotBlank
    private String prePassword;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String newPasswordConfirm;
}
