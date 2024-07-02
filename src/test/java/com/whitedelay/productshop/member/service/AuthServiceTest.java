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
import com.whitedelay.productshop.security.AES256Encoder;
import com.whitedelay.productshop.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입")
    void signup_Success() {
        // given
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

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
        } catch (Exception e) {
            fail("유효성 검사 중 예외 발생: " + e.getMessage());
        }
    }


    @Test
    @DisplayName("회원가입_유효성 검사 성공 테스트")
    void signup_ValidSignupRequestDto() {
        // given
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

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
        } catch (Exception e) {
            fail("유효성 검사 중 예외 발생: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("회원가입_유효성 검사 실패 테스트")
    void signup_InvalidSignupRequestDto() {
        // given
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

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
        } catch (Exception e) {
            fail("유효성 검사 중 예외 발생: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("회원가입_이메일 인증이 완료되지 않은 경우 회원가입 요청이 실패하는지 테스트")
    void signup_EmailNotVerified() {
        // given: 테스트에 필요한 초기 설정과 준비를 수행
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .email("testuser@example.com")
                .build();

        when(mailService.isSignupEmailVerified(signupRequestDto.getEmail())).thenReturn(false);

        // when: 테스트할 메서드를 호출하여 실제 동작을 수행
        assertThatThrownBy(() -> authService.signup(signupRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 인증을 완료해주세요.");
    }

    @Test
    @DisplayName("회원가입_중복된 아이디가 있는 경우 회원가입 요청이 실패하는지 테스트")
    void signup_DuplicateMemberId() {
        // given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .memberId("testuser")
                .email("testuser@example.com")
                .build();

        when(mailService.isSignupEmailVerified(signupRequestDto.getEmail())).thenReturn(true);
        when(memberRepository.existsByMemberId(signupRequestDto.getMemberId())).thenReturn(true);

        // when: 테스트할 메서드를 호출하여 실제 동작을 수행
        assertThatThrownBy(() -> authService.signup(signupRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 아이디입니다.");
    }

    @Test
    @DisplayName("로그인")
    void login_Success() {
        // given: 테스트에 필요한 초기 설정과 준비를 수행
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .memberId("testuser")
                .password("password")
                .build();

        Member member = Member.builder()
                .memberId("testuser")
                .password("encodedPassword")
                .role(MemberRoleEnum.USER)
                .build();
        ReflectionTestUtils.setField(authService, "REFRESH_TOKEN_TIME", 123456L);

        when(memberRepository.findByMemberId(loginRequestDto.getMemberId())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())).thenReturn(true);
        when(jwtUtil.createAccessToken(ArgumentMatchers.anyString(), any(MemberRoleEnum.class))).thenReturn("accessToken");
        when(jwtUtil.createRefreshToken()).thenReturn("refreshToken");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when: 테스트할 메서드를 호출하여 실제 동작을 수행
        LoginResponseDto response = authService.login(loginRequestDto, httpServletResponse);

        // then: 테스트 결과를 검증합니다.
        assertThat(response.getMemberId()).isEqualTo("testuser");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        verify(jwtUtil).addJwtToCookie("accessToken", httpServletResponse);
        verify(valueOperations).set("testuser", "refreshToken", 123456L, TimeUnit.MINUTES); // 이 부분 변경
    }

    @Test
    @DisplayName("로그인_잘못된 사용자인지 확인하는 테스트")
    void login_InvalidUser() {
        // given: 테스트에 필요한 초기 설정과 준비를 수행합니다.
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .memberId("invaliduser")
                .password("password")
                .build();

        when(memberRepository.findByMemberId(loginRequestDto.getMemberId())).thenReturn(Optional.empty());

        // when: 테스트할 메서드를 호출하여 실제 동작을 수행합니다.
        assertThatThrownBy(() -> authService.login(loginRequestDto, httpServletResponse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 사용자 아이디 또는 비밀번호입니다.");
    }

    @Test
    @DisplayName("로그인_잘못된 비밀번호인지 확인하는 테스트")
    void login_InvalidPassword() {
        // given: 테스트에 필요한 초기 설정과 준비를 수행합니다.
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .memberId("testuser")
                .password("wrongpassword")
                .build();

        Member member = Member.builder()
                .memberId("testuser")
                .password("encodedPassword")
                .role(MemberRoleEnum.USER)
                .build();

        when(memberRepository.findByMemberId(loginRequestDto.getMemberId())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())).thenReturn(false);

        // when: 테스트할 메서드를 호출하여 실제 동작을 수행합니다.
        assertThatThrownBy(() -> authService.login(loginRequestDto, httpServletResponse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 사용자 아이디 또는 비밀번호입니다.");
    }

    @Test
    @DisplayName("로그인_토큰 생성 실패 시 예외가 발생하는지 테스트")
    void login_TokenCreationFailed() {
        // given: 테스트에 필요한 초기 설정과 준비를 수행합니다.
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .memberId("testuser")
                .password("password")
                .build();

        Member member = Member.builder()
                .memberId("testuser")
                .password("encodedPassword")
                .role(MemberRoleEnum.USER)
                .build();

        when(memberRepository.findByMemberId(loginRequestDto.getMemberId())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())).thenReturn(true);
        when(jwtUtil.createAccessToken(ArgumentMatchers.anyString(), any(MemberRoleEnum.class))).thenThrow(new TokenCreationException("Access token creation failed"));

        // when: 테스트할 메서드를 호출하여 실제 동작을 수행합니다.
        assertThatThrownBy(() -> authService.login(loginRequestDto, httpServletResponse))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("토큰 발급에 실패했습니다.");
    }


    @Test
    @DisplayName("리프레시 토큰")
    void refreshToken_Success() {
        // given: 테스트에 필요한 초기 설정과 준비를 수행
        String memberId = "testuser";
        String validRefreshToken = "validRefreshToken";
        Member member = Member.builder()
                .memberId(memberId)
                .role(MemberRoleEnum.USER)
                .build();

        when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.of(member));
        when(jwtUtil.substringToken(validRefreshToken)).thenReturn(validRefreshToken);
        when(jwtUtil.validateToken(validRefreshToken)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(memberId)).thenReturn(validRefreshToken);
        when(jwtUtil.createAccessToken(memberId, member.getRole())).thenReturn("newAccessToken");

        // when: 테스트할 메서드를 호출하여 실제 동작을 수행
        RefreshTokenResponseDto response = authService.refreshToken(memberId, validRefreshToken, httpServletResponse);

        // then: 테스트 결과를 검증합니다.
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getMemberId()).isEqualTo(memberId);
        verify(jwtUtil).addJwtToCookie("newAccessToken", httpServletResponse);
    }

    @Test
    @DisplayName("리프레시 토큰_유효하지 않은 리프레시 토큰 요청 시 예외가 발생하는지 테스트")
    void refreshToken_InvalidToken() {
        // given: 테스트에 필요한 초기 설정과 준비를 수행
        String memberId = "testuser";
        String invalidRefreshToken = "invalidRefreshToken";
        Member member = Member.builder()
                .memberId(memberId)
                .role(MemberRoleEnum.USER)
                .build();

        when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.of(member));
        when(jwtUtil.substringToken(invalidRefreshToken)).thenReturn(invalidRefreshToken);
        when(jwtUtil.validateToken(invalidRefreshToken)).thenReturn(false);

        // when: 테스트할 메서드를 호출하여 실제 동작을 수행
        assertThatThrownBy(() -> authService.refreshToken(memberId, invalidRefreshToken, httpServletResponse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("만료된 RefreshToken입니다.");

        // then: 추가 검증
        verify(memberService).deleteCookie(httpServletResponse);
    }

    @Test
    @DisplayName("리프레시 토큰_리프레시 토큰이 Redis에 저장된 토큰과 일치하지 않는 경우 예외가 발생하는지 테스트")
    void refreshToken_TokenMismatch() {
        // given: 테스트에 필요한 초기 설정과 준비를 수행
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

        // when: 테스트할 메서드를 호출하여 실제 동작을 수행
        assertThatThrownBy(() -> authService.refreshToken(memberId, refreshToken, httpServletResponse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 RefreshToken입니다.");

        // then: 추가 검증
        verify(memberService).deleteCookie(httpServletResponse);
    }
}
