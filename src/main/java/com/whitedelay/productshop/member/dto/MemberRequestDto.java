package com.whitedelay.productshop.member.dto;

import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberRequestDto {

    private String memberid;

    private String password;

    private String email;

    private String username;

    private String address;

    private String phone;

    private MemberRoleEnum role;

}