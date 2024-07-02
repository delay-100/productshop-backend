package com.whitedelay.productshop.member.dto;

import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MemberRequestDto {
    @NotBlank
    private String memberId;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    @NotBlank
    private String memberName;
    @NotBlank
    private String address;
    @NotBlank
    private int zipCode;
    @NotBlank
    private String phone;
    @NotBlank
    private MemberRoleEnum role;
}