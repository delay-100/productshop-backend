package com.whitedelay.productshop.mail.service;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.util.AES256Encoder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AES256Encoder aes256Encoder;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mailService, "SIGNUP_CODE_KEY_PREFIX", "signup:");
        ReflectionTestUtils.setField(mailService, "SIGNUP_CODE_KEY_CHECK", ":checked");
        ReflectionTestUtils.setField(mailService, "mailUsername", "test@example.com");
    }

    @Test
    @DisplayName("회원가입 인증 이메일 전송")
    void postSignupVerificationEmail_Success() throws MessagingException, UnsupportedEncodingException {
        // given
        String email = "test@example.com";
        when(memberRepository.findByEmail(aes256Encoder.encodeString(email))).thenReturn(Optional.empty());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // when
        boolean result = mailService.postSignupVerificationEmail(email);

        // then
        assertAll(
                () -> assertThat(result).isTrue(),
                () -> verify(javaMailSender).send(mimeMessage),
                () -> verify(valueOperations).set(eq("signup:" + email), anyString(), eq(10L), eq(TimeUnit.MINUTES))
        );
    }

    @Test
    @DisplayName("회원가입 인증 이메일 전송 실패 - 이미 가입된 이메일")
    void postSignupVerificationEmail_EmailAlreadyExists() {
        // given
        String email = "test@example.com";
        when(memberRepository.findByEmail(aes256Encoder.encodeString(email))).thenReturn(Optional.of(new Member()));

        // when & then
        assertThatThrownBy(() -> mailService.postSignupVerificationEmail(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 이메일입니다.");
    }

    @Test
    @DisplayName("회원가입 인증 이메일 코드 확인")
    void checkSignupEmailCode_Success() {
        // given
        String email = "test@example.com";
        String verificationCode = "12345678";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("signup:test@example.com")).thenReturn(verificationCode);

        // when
        boolean result = mailService.checkSignupEmailCode(email, verificationCode);

        // then
        assertAll(
                () -> assertThat(result).isTrue(),
                () -> verify(valueOperations).set("signup:test@example.com", verificationCode + ":checked", 10, TimeUnit.MINUTES)
        );
    }

    @Test
    @DisplayName("회원가입 인증 이메일 코드 확인 실패 - 코드 불일치")
    void checkSignupEmailCode_CodeMismatch() {
        // given
        String email = "test@example.com";
        String verificationCode = "12345678";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("signup:test@example.com")).thenReturn("87654321");

        // when
        boolean result = mailService.checkSignupEmailCode(email, verificationCode);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("회원가입 인증 이메일 확인")
    void isSignupEmailVerified_Success() {
        // given
        String email = "test@example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("signup:test@example.com")).thenReturn("12345678:checked");

        // when
        boolean result = mailService.isSignupEmailVerified(email);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원가입 인증 이메일 확인 실패 - 인증되지 않음")
    void isSignupEmailVerified_NotVerified() {
        // given
        String email = "test@example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("signup:test@example.com")).thenReturn("12345678");

        // when
        boolean result = mailService.isSignupEmailVerified(email);

        // then
        assertThat(result).isFalse();
    }
}
