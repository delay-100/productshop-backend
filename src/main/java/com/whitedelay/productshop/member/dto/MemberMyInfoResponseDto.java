package com.whitedelay.productshop.member.dto;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.security.AES256Encoder;
import jakarta.validation.constraints.NotBlank;
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
    private int zipCode;
    private String phone;

    public static MemberMyInfoResponseDto from(Member member, AES256Encoder aes256Encoder) {
        return MemberMyInfoResponseDto.builder()
                .memberId(member.getMemberId())
                .email(aes256Encoder.decodeString(member.getEmail()))
                .memberName(aes256Encoder.decodeString(member.getMemberName()))
                .address(aes256Encoder.decodeString(member.getAddress()))
                .zipCode(member.getZipCode())
                .phone(aes256Encoder.decodeString(member.getPhone()))
                .build();
    }

}
