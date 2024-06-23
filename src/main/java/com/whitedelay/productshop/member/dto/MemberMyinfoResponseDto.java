package com.whitedelay.productshop.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberMyinfoResponseDto {
    private String userid;
    private String email;
    private String username;
    private String address;
    private String phone;
}
