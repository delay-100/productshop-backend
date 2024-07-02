package com.whitedelay.productshop.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberMyInfoResponseDto {
    private String memberId;
    private String email;
    private String memberName;
    private String address;
    private String zipCode;
    private String phone;
}
