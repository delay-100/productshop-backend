package com.whitedelay.productshop.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequestDto {
    @NotBlank(message = "아이디를 입력해주세요.")
    private String memberId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    private String memberName;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    private String phone;
    // 관리자 권한은 아직 필요 없으니 권한은 모두 User로 받을 것임!

    @NotBlank(message = "이메일 코드를 입력해주세요.")
    private String emailCode;

}
