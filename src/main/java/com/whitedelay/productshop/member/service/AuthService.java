package com.whitedelay.productshop.member.service;

import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.mail.service.RedisService;
import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.security.AES256Encoder;
import com.whitedelay.productshop.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AES256Encoder aes256Encoder;
    private final JwtUtil jwtUtil;

    public boolean signup(SignupRequestDto signupRequestDto) {
        // Redis 검증
        if (!redisService.isEmailVerified(signupRequestDto.getEmail())) {
            return false;
        }
        if (memberRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }
        if (memberRepository.existsByMemberId(signupRequestDto.getMemberId())) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }
        // 값 넣기
        String memberId = signupRequestDto.getMemberId();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String email = aes256Encoder.encodeString(signupRequestDto.getEmail());
        String memberName = aes256Encoder.encodeString(signupRequestDto.getMemberName());
        String address = aes256Encoder.encodeString(signupRequestDto.getAddress());
        String phone = aes256Encoder.encodeString(signupRequestDto.getPhone());

        MemberRequestDto memberRequestDto = new MemberRequestDto(memberId, password, email, memberName, address, phone, MemberRoleEnum.USER);
        memberRepository.save(Member.from(memberRequestDto));
        return true;
    }

    public MemberMyinfoResponseDto getMemberMyinfo(String userToken) {
        // 토큰 내의 유저 빼오기
        userToken = jwtUtil.substringToken(userToken);
        String memberId = jwtUtil.getMemberInfoFromToken(userToken).getSubject();
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return MemberMyinfoResponseDto.builder()
                .memberId(member.getMemberId())
                .email(aes256Encoder.decodeString(member.getEmail()))
                .memberName(aes256Encoder.decodeString(member.getMemberName()))
                .address(aes256Encoder.decodeString(member.getAddress()))
                .phone(aes256Encoder.decodeString(member.getPhone()))
                .build();
    }

    public MemberMyinfoResponseDto updateMemberMyinfo(String userToken, MemberMyinfoRequestDto memberMyinfoRequestDto) {
        // 토큰 내의 유저 빼오기
        userToken = jwtUtil.substringToken(userToken);
        String memberId = jwtUtil.getMemberInfoFromToken(userToken).getSubject();
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String encodedAddress = aes256Encoder.encodeString(memberMyinfoRequestDto.getAddress());
        String encodedPhone = aes256Encoder.encodeString(memberMyinfoRequestDto.getPhone());

        member.setAddress(encodedAddress);
        member.setPhone(encodedPhone);

        memberRepository.save(member);

        return MemberMyinfoResponseDto.builder()
                .memberId(member.getMemberId())
                .email(aes256Encoder.decodeString(member.getEmail()))
                .memberName(aes256Encoder.decodeString(member.getMemberName()))
                .address(aes256Encoder.decodeString(member.getAddress()))
                .phone(aes256Encoder.decodeString(member.getPhone()))
                .build();

    }

    public boolean updateMemberPassword(String userToken, MemberpasswordRequestDto memberpasswordRequestDto) {
        // 토큰 내의 유저 빼오기
        userToken = jwtUtil.substringToken(userToken);
        String memberId = jwtUtil.getMemberInfoFromToken(userToken).getSubject();
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(memberpasswordRequestDto.getPrePassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호 확인
        if (!memberpasswordRequestDto.getNewPassword().equals(memberpasswordRequestDto.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("새로운 비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호 암호화 및 설정
        String newPassword = passwordEncoder.encode(memberpasswordRequestDto.getNewPassword());
        member.setPassword(newPassword);

        memberRepository.save(member);

        return true;
    }
}