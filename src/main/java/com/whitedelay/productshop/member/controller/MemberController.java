package com.whitedelay.productshop.member.controller;

import com.whitedelay.productshop.member.dto.MemberMyInfoRequestDto;
import com.whitedelay.productshop.member.dto.MemberMyInfoResponseDto;
import com.whitedelay.productshop.member.dto.MemberPasswordRequestDto;
import com.whitedelay.productshop.member.service.MemberService;
import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.util.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    private static final String BASE_MEMBER = "/member";

    /**
     * DELETE
     * 로그아웃
     * @param userDetails security의 회원 정보
     * @param res 서블릿 응답 객체
     * @return 로그아웃 성공 여부(T/F)
     */
    @DeleteMapping(BASE_MEMBER + "/logout")
    public ApiResponse<Boolean> logout(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletResponse res
    ) {
        return ApiResponse.createSuccess(memberService.logout(userDetails.getMember(), res));
    }

    /**
     * GET
     * 회원 정보 업데이트
     * @param userDetails security의 회원 정보
     * @return 회원 정보 응답 객체 DTO
     */
    @GetMapping(BASE_MEMBER + "/myinfo")
    public ApiResponse<MemberMyInfoResponseDto> getMemberMyInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ApiResponse.createSuccess(memberService.getMemberMyInfo(userDetails.getMember()));
    }

    /**
     * PATCH
     * 회원 정보 변경 1 - 주소, 전화번호
     * @param userDetails security의 회원 정보
     * @param memberMyInfoRequestDto 회원 정보 요청 객체 DTO
     * @return 회원 정보 응답 객체 DTO
     */
    @PatchMapping(BASE_MEMBER + "/myinfo")
    public ApiResponse<MemberMyInfoResponseDto> updateMemberMyInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MemberMyInfoRequestDto memberMyInfoRequestDto
    ) {
        return ApiResponse.createSuccess(memberService.updateMemberMyInfo(userDetails.getMember(), memberMyInfoRequestDto));
    }

    /**
     * PATCH
     * 회원 정보 변경 - 비밀번호
     * @param userDetails security의 회원 정보
     * @param memberPasswordRequestDto 회원 비밀번호 요청 DTO
     * @return 비밀번호 변경 성공 여부(T/F)
     */
    @PatchMapping(BASE_MEMBER + "/modify/password")
    public ApiResponse<Boolean> updateMemberPassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MemberPasswordRequestDto memberPasswordRequestDto
    ) {
        return ApiResponse.createSuccess(memberService.updateMemberPassword(userDetails.getMember(), memberPasswordRequestDto));
    }

}
