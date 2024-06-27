package com.whitedelay.productshop.member.dto;

import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberRequestDto {

    private String memberId;

    private String password;

    private String email;

    private String memberName;

    private String address;

    private String zipCode;

    private String phone;

    private MemberRoleEnum role;

}