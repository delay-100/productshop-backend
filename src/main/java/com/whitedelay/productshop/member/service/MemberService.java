package com.whitedelay.productshop.member.service;

import com.whitedelay.productshop.member.dto.SignupRequestDto;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import com.whitedelay.productshop.mail.service.RedisService;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.security.AES256Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MemberService {
    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AES256Encoder aes256Encoder;

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

    public SignupRequestDto tempCheckUser(String id, String password) {
        Member member = memberRepository.findByMemberid(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (passwordEncoder.matches(password, member.getPassword())) {
            SignupRequestDto signupRequestDto = new SignupRequestDto();
            signupRequestDto.setUserid(member.getMemberid());
            signupRequestDto.setEmail(aes256Encoder.decodeString(member.getEmail()));
            signupRequestDto.setUsername(aes256Encoder.decodeString(member.getMembername()));
            signupRequestDto.setAddress(aes256Encoder.decodeString(member.getAddress()));
            signupRequestDto.setPhone(aes256Encoder.decodeString(member.getPhone()));
            return signupRequestDto;
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

}
