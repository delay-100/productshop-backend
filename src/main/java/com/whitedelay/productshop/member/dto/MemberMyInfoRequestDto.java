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
public class MemberMyInfoRequestDto {
    @NotBlank
    private String address;
    @NotBlank
    private String phone;
    @NotBlank
    private int zipCode;
}
