package com.whitedelay.productshop.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberPasswordRequestDto {
    @NotBlank
    private String prePassword;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String newPasswordConfirm;
}
