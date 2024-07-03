package com.whitedelay.productshop.member.service;

import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.security.AES256Encoder;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AES256Encoder aes256Encoder;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("로그아웃")
    void logout_Success() {
        // given
        Member member = Member.builder()
                .memberId("testuser")
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(member.getMemberId())).thenReturn("someToken");
        ReflectionTestUtils.setField(memberService, "AUTHORIZATION_HEADER", "Auth");
        ReflectionTestUtils.setField(memberService, "REFRESHTOKEN_HEADER", "Refresh");

        // when
        boolean result = memberService.logout(member, httpServletResponse);

        // then
        assertAll(
                () -> assertThat(result).isTrue(),
                () -> verify(redisTemplate).delete(member.getMemberId()),
                () -> verify(httpServletResponse).addCookie(argThat(cookie ->
                        cookie.getName().equals("Auth") && cookie.getMaxAge() == 0
                )),
                () -> verify(httpServletResponse).addCookie(argThat(cookie ->
                        cookie.getName().equals("Refresh") && cookie.getMaxAge() == 0
                ))
        );
    }

    @Test
    @DisplayName("내 정보 조회")
    void getMemberMyInfo_Success() {
        // given
        Member member = Member.builder()
                .memberId("testuser")
                .email("encodedEmail")
                .memberName("encodedName")
                .address("encodedAddress")
                .zipCode(12345)
                .phone("encodedPhone")
                .build();

        when(aes256Encoder.decodeString(member.getEmail())).thenReturn("test@example.com");
        when(aes256Encoder.decodeString(member.getMemberName())).thenReturn("홍길동");
        when(aes256Encoder.decodeString(member.getAddress())).thenReturn("서울시 강남구");
        when(aes256Encoder.decodeString(member.getPhone())).thenReturn("010-1234-5678");

        // when
        MemberMyInfoResponseDto responseDto = memberService.getMemberMyInfo(member);

        // then
        assertAll(
                () -> assertThat(responseDto.getEmail()).isEqualTo("test@example.com"),
                () -> assertThat(responseDto.getMemberName()).isEqualTo("홍길동"),
                () -> assertThat(responseDto.getAddress()).isEqualTo("서울시 강남구"),
                () -> assertThat(responseDto.getPhone()).isEqualTo("010-1234-5678")
        );
    }

    @Test
    @DisplayName("내 정보 수정")
    void updateMemberMyInfo_Success() {
        // given
        Member member = Member.builder()
                .memberId("testuser")
                .email("encodedEmail")
                .memberName("encodedName")
                .address("encodedAddress")
                .zipCode(12345)
                .phone("encodedPhone")
                .build();

        MemberMyInfoRequestDto requestDto = MemberMyInfoRequestDto.builder()
                .address("서울시 서초구")
                .phone("010-5678-1234")
                .zipCode(12345)
                .build();

        ReflectionTestUtils.setField(aes256Encoder, "ALG", "AES/GCM/NoPadding");
        ReflectionTestUtils.setField(aes256Encoder, "KEY", "12345678901234567890123456789012");

        // AES256Encoder의 encodeString 메소드 모의 설정
        when(aes256Encoder.encodeString(requestDto.getAddress())).thenReturn("encodedAddress");
        when(aes256Encoder.encodeString(requestDto.getPhone())).thenReturn("encodedPhone");

        // AES256Encoder의 decodeString 메소드 모의 설정
        when(aes256Encoder.decodeString("encodedEmail")).thenReturn("test@example.com");
        when(aes256Encoder.decodeString("encodedName")).thenReturn("홍길동");
        when(aes256Encoder.decodeString("encodedAddress")).thenReturn("서울시 서초구");
        when(aes256Encoder.decodeString("encodedPhone")).thenReturn("010-5678-1234");

        // MemberRepository의 findByMemberId 메소드 모의 설정
        when(memberRepository.findByMemberId(member.getMemberId())).thenReturn(Optional.of(member));

        // when
        MemberMyInfoResponseDto responseDto = memberService.updateMemberMyInfo(member, requestDto);

        // then
        assertAll(
                () -> assertThat(responseDto.getEmail()).isEqualTo("test@example.com"),
                () -> assertThat(responseDto.getMemberName()).isEqualTo("홍길동"),
                () -> assertThat(responseDto.getAddress()).isEqualTo("서울시 서초구"),
                () -> assertThat(responseDto.getPhone()).isEqualTo("010-5678-1234")
        );
    }

    @Test
    @DisplayName("비밀번호 변경_현재 비밀번호 불일치")
    void updateMemberPassword_InvalidCurrentPassword() {
        // given
        Member member = Member.builder()
                .memberId("testuser")
                .password("encodedPassword")
                .build();

        MemberPasswordRequestDto requestDto = MemberPasswordRequestDto.builder()
                .prePassword("wrongPassword")
                .newPassword("newPassword1")
                .newPasswordConfirm("newPassword1")
                .build();

        when(passwordEncoder.matches(requestDto.getPrePassword(), member.getPassword())).thenReturn(false);

        // MemberRepository의 findByMemberId 메소드 모의 설정
        when(memberRepository.findByMemberId(member.getMemberId())).thenReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> memberService.updateMemberPassword(member, requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("비밀번호 변경_새로운 비밀번호 불일치")
    void updateMemberPassword_NewPasswordMismatch() {
        // given
        Member member = Member.builder()
                .memberId("testuser")
                .password("encodedPassword")
                .build();

        MemberPasswordRequestDto requestDto = MemberPasswordRequestDto.builder()
                .prePassword("encodedPassword")
                .newPassword("newPassword1")
                .newPasswordConfirm("differentPassword")
                .build();

        when(passwordEncoder.matches(requestDto.getPrePassword(), member.getPassword())).thenReturn(true);

        // MemberRepository의 findByMemberId 메소드 모의 설정
        when(memberRepository.findByMemberId(member.getMemberId())).thenReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> memberService.updateMemberPassword(member, requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("새로운 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("비밀번호 변경_성공")
    void updateMemberPassword_Success() {
        // given
        Member member = Member.builder()
                .memberId("testuser")
                .password("encodedPassword")
                .build();

        MemberPasswordRequestDto requestDto = MemberPasswordRequestDto.builder()
                .prePassword("encodedPassword")
                .newPassword("newPassword1")
                .newPasswordConfirm("newPassword1")
                .build();

        when(passwordEncoder.matches(requestDto.getPrePassword(), member.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(requestDto.getNewPassword())).thenReturn("newEncodedPassword");

        // MemberRepository의 findByMemberId 메소드 모의 설정
        when(memberRepository.findByMemberId(member.getMemberId())).thenReturn(Optional.of(member));

        // when
        boolean result = memberService.updateMemberPassword(member, requestDto);

        // then
        assertAll(
                () -> assertThat(result).isTrue(),
                () -> assertThat(member.getPassword()).isEqualTo("newEncodedPassword")
        );
    }

    @Test
    @DisplayName("쿠키 삭제")
    void deleteCookie_Success() {
        // given
        ReflectionTestUtils.setField(memberService, "AUTHORIZATION_HEADER", "Auth");
        ReflectionTestUtils.setField(memberService, "REFRESHTOKEN_HEADER", "Refresh");

        // when
        memberService.deleteCookie(httpServletResponse);

        // then
        assertAll(
                () -> verify(httpServletResponse).addCookie(argThat(cookie ->
                        cookie.getName().equals("Auth") && cookie.getMaxAge() == 0
                )),
                () -> verify(httpServletResponse).addCookie(argThat(cookie ->
                        cookie.getName().equals("Refresh") && cookie.getMaxAge() == 0
                ))
        );
    }
}
