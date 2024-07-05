package com.whitedelay.productshop.member.service;

import com.whitedelay.productshop.exception.TokenCreationException;
import com.whitedelay.productshop.mail.service.MailService;
import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.util.AES256Encoder;
import com.whitedelay.productshop.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberService memberService;
    private final MailService mailService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AES256Encoder aes256Encoder;
    private final JwtUtil jwtUtil;

    @Value("${REFRESH_TOKEN_TIME}")
    private Long REFRESH_TOKEN_TIME;

    @Transactional
    public Boolean signup(SignupRequestDto signupRequestDto) {
        if (!mailService.isSignupEmailVerified(signupRequestDto.getEmail())) {
            throw new IllegalArgumentException("이메일 인증을 완료해주세요.");
        }
        if (memberRepository.existsByMemberId(signupRequestDto.getMemberId())) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }

        memberRepository.save(Member.from(signupRequestDto, passwordEncoder, aes256Encoder));
        return true;
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse res) {
        Member member = memberRepository.findByMemberId(loginRequestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 아이디 또는 비밀번호입니다."));

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 사용자 아이디 또는 비밀번호입니다.");
        }

        try {
            // accessToken 발급
            String accessToken = jwtUtil.createAccessToken(member.getId(), member.getMemberId(), member.getRole());
            jwtUtil.addJwtToCookie(accessToken, res);

            // refreshToken 발급
            String refreshToken = jwtUtil.createRefreshToken();
            redisTemplate.opsForValue().set(member.getMemberId(), refreshToken, REFRESH_TOKEN_TIME, TimeUnit.MINUTES);

            return LoginResponseDto.from(member.getMemberId(), refreshToken);
        } catch (TokenCreationException e) {
            throw new TokenCreationException("토큰 발급에 실패했습니다.", e);
        }
    }

    public RefreshTokenResponseDto refreshToken(String memberId, String refreshToken, HttpServletResponse res) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 아이디 또는 비밀번호입니다."));

        // RefreshToken 기간 검증
        refreshToken = jwtUtil.substringToken(refreshToken);
        if (!jwtUtil.validateToken(refreshToken)) {
            memberService.deleteCookie(res);
            throw new IllegalArgumentException("만료된 RefreshToken입니다.");
        }

        // redis의 token과 현재 요청토큰이 같은지 확인
        String redisToken = redisTemplate.opsForValue().get(member.getMemberId());
        if (!refreshToken.equals(jwtUtil.substringToken(redisToken))) {
            memberService.deleteCookie(res);
            throw new IllegalArgumentException("유효하지 않은 RefreshToken입니다.");
        }

        // AccessToken 재발급
        String accessToken = jwtUtil.createAccessToken(member.getId(), member.getMemberId(), member.getRole());
        jwtUtil.addJwtToCookie(accessToken, res);

        return RefreshTokenResponseDto.from(memberId, accessToken);
    }

}