package com.whitedelay.productshop.member.service;

import com.whitedelay.productshop.mail.service.MailService;
import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.security.AES256Encoder;
import com.whitedelay.productshop.security.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Value("${AUTHORIZATION_HEADER}")
    public String AUTHORIZATION_HEADER;

    @Value("${REFRESHTOKEN_HEADER}")

    public String REFRESHTOKEN_HEADER;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AES256Encoder aes256Encoder;

//    redis의 토큰 삭제 & UserCookie비워주기
    public boolean logout(Member member, HttpServletResponse res) {
        // redis에서 memberId 찾아서 삭제
        String refreshToken = redisTemplate.opsForValue().get(member.getMemberId());
        if (refreshToken != null) {
            redisTemplate.delete(member.getMemberId());
        }
        deleteCookie(res);

        return true;
    }


    public MemberMyInfoResponseDto getMemberMyInfo(Member member) {
        return MemberMyInfoResponseDto.builder()
                .memberId(member.getMemberId())
                .email(aes256Encoder.decodeString(member.getEmail()))
                .memberName(aes256Encoder.decodeString(member.getMemberName()))
                .address(aes256Encoder.decodeString(member.getAddress()))
                .zipCode(aes256Encoder.decodeString(member.getZipCode()))
                .phone(aes256Encoder.decodeString(member.getPhone()))
                .build();
    }

    public MemberMyInfoResponseDto updateMemberMyInfo(Member member, MemberMyInfoRequestDto memberMyinfoRequestDto) {
        String encodedAddress = aes256Encoder.encodeString(memberMyinfoRequestDto.getAddress());
        String encodedPhone = aes256Encoder.encodeString(memberMyinfoRequestDto.getPhone());

        member.setAddress(encodedAddress);
        member.setPhone(encodedPhone);

        memberRepository.save(member);

        return MemberMyInfoResponseDto.builder()
                .memberId(member.getMemberId())
                .email(aes256Encoder.decodeString(member.getEmail()))
                .memberName(aes256Encoder.decodeString(member.getMemberName()))
                .address(aes256Encoder.decodeString(member.getAddress()))
                .phone(aes256Encoder.decodeString(member.getPhone()))
                .build();
    }

    public boolean updateMemberPassword(Member member, MemberPasswordRequestDto memberPasswordRequestDto) {

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(memberPasswordRequestDto.getPrePassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호 확인
        if (!memberPasswordRequestDto.getNewPassword().equals(memberPasswordRequestDto.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("새로운 비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호 암호화 및 설정
        String newPassword = passwordEncoder.encode(memberPasswordRequestDto.getNewPassword());
        member.setPassword(newPassword);

        memberRepository.save(member);

        return true;
    }

    private void deleteCookie(HttpServletResponse res) {

        // 응답헤더 Cookie 비우기
        Cookie accessTokenCookie = new Cookie(AUTHORIZATION_HEADER, null);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie(REFRESHTOKEN_HEADER, null);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(0);

        res.addCookie(accessTokenCookie);
        res.addCookie(refreshTokenCookie);

    }
}