package com.whitedelay.productshop.member.dto;

import lombok.Getter;

@Getter
public class MemberpasswordRequestDto {
    private String prePassword;
    private String newPassword;
    private String newPasswordConfirm;
}
