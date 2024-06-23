package com.whitedelay.productshop.member.service;

import com.whitedelay.productshop.member.dto.MemberMyinfoRequestDto;
import com.whitedelay.productshop.member.dto.MemberMyinfoResponseDto;
import com.whitedelay.productshop.member.dto.SignupRequestDto;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import com.whitedelay.productshop.mail.service.RedisService;
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
        if (memberRepository.existsByMemberid(signupRequestDto.getUserid())) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }
        // 값 넣기
        String userid = signupRequestDto.getUserid();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String email = aes256Encoder.encodeString(signupRequestDto.getEmail());
        String username = aes256Encoder.encodeString(signupRequestDto.getUsername());
        String address = aes256Encoder.encodeString(signupRequestDto.getAddress());
        String phone = aes256Encoder.encodeString(signupRequestDto.getPhone());

        Member member = new Member(userid, password, email, username, address, phone, MemberRoleEnum.USER);

        memberRepository.save(member);
        return true;
    }

    public MemberMyinfoResponseDto getMemberMyinfo(String userToken) {
        // 토큰 내의 유저 빼오기
        userToken = jwtUtil.substringToken(userToken);
        String memberId = jwtUtil.getMemberInfoFromToken(userToken).getSubject();
        Member member = memberRepository.findByMemberid(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return MemberMyinfoResponseDto.builder()
                .userid(member.getMemberid())
                .email(aes256Encoder.decodeString(member.getEmail()))
                .username(aes256Encoder.decodeString(member.getMembername()))
                .address(aes256Encoder.decodeString(member.getAddress()))
                .phone(aes256Encoder.decodeString(member.getPhone()))
                .build();
    }

    public MemberMyinfoResponseDto updateMemberMyinfo(String userToken, MemberMyinfoRequestDto memberMyinfoRequestDto) {
        // 토큰 내의 유저 빼오기
        userToken = jwtUtil.substringToken(userToken);
        String memberId = jwtUtil.getMemberInfoFromToken(userToken).getSubject();
        Member member = memberRepository.findByMemberid(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));


        String encodedAddress = aes256Encoder.encodeString(memberMyinfoRequestDto.getAddress());
        String encodedPhone = aes256Encoder.encodeString(memberMyinfoRequestDto.getPhone());

        member.setAddress(encodedAddress);
        member.setPhone(encodedPhone);

        memberRepository.save(member);

        return MemberMyinfoResponseDto.builder()
                .userid(member.getMemberid())
                .email(aes256Encoder.decodeString(member.getEmail()))
                .username(aes256Encoder.decodeString(member.getMembername()))
                .address(aes256Encoder.decodeString(member.getAddress()))
                .phone(aes256Encoder.decodeString(member.getPhone()))
                .build();

    }
}