package com.whitedelay.productshop.member.service;

import com.whitedelay.exception.TokenCreationException;
import com.whitedelay.productshop.mail.service.MailService;
import com.whitedelay.productshop.member.dto.LoginRequestDto;
import com.whitedelay.productshop.member.dto.LoginResponseDto;
import com.whitedelay.productshop.member.dto.RefreshTokenResponseDto;
import com.whitedelay.productshop.member.dto.SignupRequestDto;
import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MailService mailService;

    @Mock
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("회원가입 - 유효한 요청")
    void signup_Success() {
        // given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .memberId("validUser123")
                .password("Password1")
                .email("valid@example.com")
                .memberName("홍길동")
                .address("서울시 강남구")
                .zipCode(12345)
                .phone("010-1234-5678")
                .role(MemberRoleEnum.USER)
                .emailCode("abc12345")
                .build();

        // when
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("회원가입 - 유효성 검사 실패")
    void signup_InvalidSignupRequestDto() {
        // given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .memberId("inv@lid")
                .password("pass")
                .email("invalid-email")
                .memberName("")
                .address("")
                .zipCode(1234) // Invalid zip code
                .phone("01012345678")
                .role(null) // Null role
                .emailCode("abcd") // Invalid email code
                .build();

        // when
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);

        // then
        assertThat(violations).hasSize(9);

        // assertAll 사용하여 모든 검증 메시지를 확인
        assertAll("유효성 검사 메시지 확인",
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("memberId") && v.getMessage().equals("아이디는 영문자와 숫자로 이루어진 4-20자여야 합니다.")),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password") && v.getMessage().equals("비밀번호는 최소 8자, 최대 20자, 최소 하나의 문자 및 하나의 숫자로 이루어져야 합니다.")),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email") && v.getMessage().equals("유효한 이메일 주소를 입력해주세요.")),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("memberName") && v.getMessage().equals("이름을 입력해주세요.")),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("address") && v.getMessage().equals("주소를 입력해주세요.")),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("zipCode") && v.getMessage().equals("우편번호는 5자리 숫자여야 합니다.")),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("phone") && v.getMessage().equals("휴대폰 번호는 010-XXXX-XXXX 형식이어야 합니다.")),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("role") && v.getMessage().equals("역할을 입력해주세요.")),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("emailCode") && v.getMessage().equals("이메일 코드는 영문자와 숫자로 이루어진 8자여야 합니다."))
        );
    }

    @Test
    @DisplayName("회원가입 - 이메일 인증 실패")
    void signup_EmailNotVerified() {
        // given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .email("testuser@example.com")
                .build();

        when(mailService.isSignupEmailVerified(signupRequestDto.getEmail())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.signup(signupRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 인증을 완료해주세요.");
    }

    @Test
    @DisplayName("회원가입 - 중복된 아이디")
    void signup_DuplicateMemberId() {
        // given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .memberId("testuser")
                .email("testuser@example.com")
                .build();

        when(mailService.isSignupEmailVerified(signupRequestDto.getEmail())).thenReturn(true);
        when(memberRepository.existsByMemberId(signupRequestDto.getMemberId())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(signupRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 아이디입니다.");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .memberId("testuser")
                .password("password")
                .build();

        Member member = Member.builder()
                .id(1L)
                .memberId("testuser")
                .password("encodedPassword")
                .role(MemberRoleEnum.USER)
                .build();
        ReflectionTestUtils.setField(authService, "REFRESH_TOKEN_TIME", 123456L);

        when(memberRepository.findByMemberId(loginRequestDto.getMemberId())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())).thenReturn(true);
        when(jwtUtil.createAccessToken(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), any(MemberRoleEnum.class))).thenReturn("accessToken");
        when(jwtUtil.createRefreshToken()).thenReturn("refreshToken");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        LoginResponseDto response = authService.login(loginRequestDto, httpServletResponse);

        // then
        assertAll(
                () -> assertThat(response.getMemberId()).isEqualTo("testuser"),
                () -> assertThat(response.getRefreshToken()).isEqualTo("refreshToken")
        );
        verify(jwtUtil).addJwtToCookie("accessToken", httpServletResponse);
        verify(valueOperations).set("testuser", "refreshToken", 123456L, TimeUnit.MINUTES); // 이 부분 변경
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 사용자 아이디")
    void login_InvalidUser() {
        // given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .memberId("invaliduser")
                .password("password")
                .build();

        when(memberRepository.findByMemberId(loginRequestDto.getMemberId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequestDto, httpServletResponse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 사용자 아이디 또는 비밀번호입니다.");
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_InvalidPassword() {
        // given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .memberId("testuser")
                .password("wrongpassword")
                .build();

        Member member = Member.builder()
                .id(1L)
                .memberId("testuser")
                .password("encodedPassword")
                .role(MemberRoleEnum.USER)
                .build();

        when(memberRepository.findByMemberId(loginRequestDto.getMemberId())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequestDto, httpServletResponse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 사용자 아이디 또는 비밀번호입니다.");
    }

    @Test
    @DisplayName("로그인 실패 - 토큰 생성 실패")
    void login_TokenCreationFailed() {
        // given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .memberId("testuser")
                .password("password")
                .build();

        Member member = Member.builder()
                .id(1L)
                .memberId("testuser")
                .password("encodedPassword")
                .role(MemberRoleEnum.USER)
                .build();

        when(memberRepository.findByMemberId(loginRequestDto.getMemberId())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())).thenReturn(true);
        when(jwtUtil.createAccessToken(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), any(MemberRoleEnum.class))).thenThrow(new TokenCreationException("Access token creation failed"));

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequestDto, httpServletResponse))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("토큰 발급에 실패했습니다.");
    }

    @Test
    @DisplayName("리프레시 토큰 - 성공")
    void refreshToken_Success() {
        // given
        String memberId = "testuser";
        String validRefreshToken = "validRefreshToken";
        Member member = Member.builder()
                .id(1L)
                .memberId(memberId)
                .role(MemberRoleEnum.USER)
                .build();

        when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.of(member));
        when(jwtUtil.substringToken(validRefreshToken)).thenReturn(validRefreshToken);
        when(jwtUtil.validateToken(validRefreshToken)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(memberId)).thenReturn(validRefreshToken);
        when(jwtUtil.createAccessToken(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(memberId), ArgumentMatchers.eq(member.getRole()))).thenReturn("newAccessToken");

        // when
        RefreshTokenResponseDto response = authService.refreshToken(memberId, validRefreshToken, httpServletResponse);

        // then
        assertAll(
                () -> assertThat(response.getAccessToken()).isEqualTo("newAccessToken"),
                () -> assertThat(response.getMemberId()).isEqualTo(memberId)
        );
        verify(jwtUtil).addJwtToCookie("newAccessToken", httpServletResponse);
    }

    @Test
    @DisplayName("리프레시 토큰 실패 - 유효하지 않은 토큰")
    void refreshToken_InvalidToken() {
        // given
        String memberId = "testuser";
        String invalidRefreshToken = "invalidRefreshToken";
        Member member = Member.builder()
                .memberId(memberId)
                .role(MemberRoleEnum.USER)
                .build();

        when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.of(member));
        when(jwtUtil.substringToken(invalidRefreshToken)).thenReturn(invalidRefreshToken);
        when(jwtUtil.validateToken(invalidRefreshToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(memberId, invalidRefreshToken, httpServletResponse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("만료된 RefreshToken입니다.");

        // 추가 검증
        verify(memberService).deleteCookie(httpServletResponse);
    }

    @Test
    @DisplayName("리프레시 토큰 실패 - 토큰 불일치")
    void refreshToken_TokenMismatch() {
        // given
        String memberId = "testuser";
        String refreshToken = "validRefreshToken";
        String mismatchedToken = "mismatchedToken";
        Member member = Member.builder()
                .memberId(memberId)
                .role(MemberRoleEnum.USER)
                .build();

        when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.of(member));
        when(jwtUtil.substringToken(refreshToken)).thenReturn(refreshToken);
        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(memberId)).thenReturn(mismatchedToken);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(memberId, refreshToken, httpServletResponse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 RefreshToken입니다.");

        // 추가 검증
        verify(memberService).deleteCookie(httpServletResponse);
    }
}
