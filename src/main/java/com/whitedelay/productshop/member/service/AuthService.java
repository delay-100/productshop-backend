package com.whitedelay.productshop.member.service;

import com.whitedelay.productshop.mail.service.MailService;
import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.entity.MemberRoleEnum;
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

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${AUTHORIZATION_HEADER}")
    public String AUTHORIZATION_HEADER;

    @Value("${REFRESHTOKEN_HEADER}")
    
    public String REFRESHTOKEN_HEADER;
    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AES256Encoder aes256Encoder;
    private final JwtUtil jwtUtil;

    public boolean signup(SignupRequestDto signupRequestDto) {
        // 메일 검증
        if (!mailService.isSignupEmailVerified(signupRequestDto.getEmail())) {
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
        String zipCode = aes256Encoder.encodeString(signupRequestDto.getZipCode());
        String phone = aes256Encoder.encodeString(signupRequestDto.getPhone());

        MemberRequestDto memberRequestDto = new MemberRequestDto(memberId, password, email, memberName, address, zipCode, phone, MemberRoleEnum.USER);
        memberRepository.save(Member.from(memberRequestDto));
        return true;
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse res) {
        Member member = memberRepository.findByMemberId(loginRequestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 아이디 또는 비밀번호입니다."));

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 사용자 아이디 또는 비밀번호입니다.");
        }

        // accessToken 발급
        String accessToken = jwtUtil.createAccessToken(member.getMemberId(), member.getRole());
        jwtUtil.addJwtToCookie(accessToken, res);

        // refreshToken 발급
        String refreshToken = jwtUtil.createRefreshToken();
        redisTemplate.opsForValue().set(member.getMemberId(), refreshToken, 25920, TimeUnit.MINUTES);

        System.out.println("accessToken = " + accessToken);
        return LoginResponseDto.builder()
                .memberId(member.getMemberId())
                .refreshToken(refreshToken)
                .build();
    }

    public RefreshTokenResponseDto refreshToken(String memberId, String refreshToken, HttpServletResponse res) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + memberId));

        // RefreshToken 기간 검증
        refreshToken = jwtUtil.substringToken(refreshToken);
        if (!jwtUtil.validateToken(refreshToken)) {
            deleteCookie(res);
            throw new IllegalArgumentException("만료된 RefreshToken입니다.");
        }

        // redis의 token과 현재 요청토큰이 같은지 확인
        String redisToken = redisTemplate.opsForValue().get(member.getMemberId());
        if (!refreshToken.equals(jwtUtil.substringToken(redisToken))) {
            deleteCookie(res);
            throw new IllegalArgumentException("유효하지 않은 RefreshToken입니다.");
        }

        // AccessToken 재발급
        String accessToken = jwtUtil.createAccessToken(member.getMemberId(), member.getRole());
        jwtUtil.addJwtToCookie(accessToken, res);

        return RefreshTokenResponseDto.builder()
                .accessToken(accessToken)
                .memberId(member.getMemberId())
                .build();
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