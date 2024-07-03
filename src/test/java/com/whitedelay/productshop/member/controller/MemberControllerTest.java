package com.whitedelay.productshop.member.controller;

import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import com.whitedelay.productshop.member.service.MemberService;
import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    @Mock
    private HttpServletResponse response;

    @Mock
    private UserDetailsImpl userDetails;

    private Member member;

    @BeforeEach
    public void setUp() {
        member = Member.builder()
                .id(1L)
                .memberId("testUser")
                .password("encodedPassword")
                .email("encodedEmail@example.com")
                .memberName("encodedName")
                .address("encodedAddress")
                .zipCode(12345)
                .phone("encodedPhone")
                .role(MemberRoleEnum.USER)
                .build();
        when(userDetails.getMember()).thenReturn(member);
    }

    @Test
    @DisplayName("로그아웃 성공")
    public void logout_Success() {
        // Given
        when(memberService.logout(any(Member.class), any(HttpServletResponse.class))).thenReturn(true);

        // When
        ApiResponse<Boolean> response = memberController.logout(userDetails, this.response);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    @DisplayName("회원 정보 조회 성공")
    public void getMemberMyInfo_Success() {
        // Given
        MemberMyInfoResponseDto memberMyInfoResponseDto = MemberMyInfoResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .memberName(member.getMemberName())
                .address(member.getAddress())
                .zipCode(member.getZipCode())
                .phone(member.getPhone())
                .build();
        when(memberService.getMemberMyInfo(any(Member.class))).thenReturn(memberMyInfoResponseDto);

        // When
        ApiResponse<MemberMyInfoResponseDto> response = memberController.getMemberMyInfo(userDetails);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isEqualTo(memberMyInfoResponseDto);
    }

    @Test
    @DisplayName("회원 정보 업데이트 성공")
    public void updateMemberMyInfo_Success() {
        // Given
        MemberMyInfoRequestDto memberMyInfoRequestDto = MemberMyInfoRequestDto.builder()
                .address("new address")
                .phone("010-1234-5678")
                .zipCode(67890)
                .build();
        MemberMyInfoResponseDto memberMyInfoResponseDto = MemberMyInfoResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .memberName(member.getMemberName())
                .address(memberMyInfoRequestDto.getAddress())
                .zipCode(memberMyInfoRequestDto.getZipCode())
                .phone(memberMyInfoRequestDto.getPhone())
                .build();
        when(memberService.updateMemberMyInfo(any(Member.class), any(MemberMyInfoRequestDto.class))).thenReturn(memberMyInfoResponseDto);

        // When
        ApiResponse<MemberMyInfoResponseDto> response = memberController.updateMemberMyInfo(userDetails, memberMyInfoRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isEqualTo(memberMyInfoResponseDto);
    }

    @Test
    @DisplayName("비밀번호 업데이트 성공")
    public void updateMemberPassword_Success() {
        // Given
        MemberPasswordRequestDto memberPasswordRequestDto = MemberPasswordRequestDto.builder()
                .prePassword("oldPassword")
                .newPassword("newPassword123")
                .newPasswordConfirm("newPassword123")
                .build();
        when(memberService.updateMemberPassword(any(Member.class), any(MemberPasswordRequestDto.class))).thenReturn(true);

        // When
        ApiResponse<Boolean> response = memberController.updateMemberPassword(userDetails, memberPasswordRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }
}
