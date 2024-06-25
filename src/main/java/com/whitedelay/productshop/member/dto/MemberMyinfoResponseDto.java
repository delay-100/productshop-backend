package com.whitedelay.productshop.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberMyinfoResponseDto {
    private String memberId;
    private String email;
    private String memberName;
    private String address;
    private String phone;
}
