package com.whitedelay.productshop.member.dto;

import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import com.whitedelay.productshop.member.validation.ZipCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "아이디는 영문자와 숫자로 이루어진 4-20자여야 합니다.")
    private String memberId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", message = "비밀번호는 최소 8자, 최대 20자, 최소 하나의 문자 및 하나의 숫자로 이루어져야 합니다.")
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    private String memberName;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    @ZipCode
    private int zipCode;

    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "휴대폰 번호는 010-XXXX-XXXX 형식이어야 합니다.")
    private String phone;

    @NotNull(message = "역할을 입력해주세요.")
    private MemberRoleEnum role;

    @NotBlank(message = "이메일 코드를 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]{8}$", message = "이메일 코드는 영문자와 숫자로 이루어진 8자여야 합니다.")
    private String emailCode;
}
