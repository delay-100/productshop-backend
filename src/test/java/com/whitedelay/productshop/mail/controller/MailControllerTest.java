package com.whitedelay.productshop.mail.controller;

import com.whitedelay.productshop.mail.dto.SignupVerificationEmailDto;
import com.whitedelay.productshop.mail.dto.SignupVerifyCodeDto;
import com.whitedelay.productshop.mail.service.MailService;
import com.whitedelay.productshop.util.ApiResponse;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MailControllerTest {

    @InjectMocks
    private MailController mailController;

    @Mock
    private MailService mailService;

    private SignupVerificationEmailDto signupVerificationEmailDto;
    private SignupVerifyCodeDto signupVerifyCodeDto;

    @BeforeEach
    void setUp() {
        signupVerificationEmailDto = SignupVerificationEmailDto.builder()
                .email("test@example.com")
                .build();

        signupVerifyCodeDto = SignupVerifyCodeDto.builder()
                .email("test@example.com")
                .emailCode("123456")
                .build();
    }

    @Test
    @DisplayName("회원가입 이메일 전송 성공")
    void postSignupVerificationEmail_Success() throws MessagingException, UnsupportedEncodingException {
        // Given
        when(mailService.postSignupVerificationEmail(any(String.class)))
                .thenReturn(true);

        // When
        ApiResponse<Boolean> response = mailController.postSignupVerificationEmail(signupVerificationEmailDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    @DisplayName("회원가입 이메일 코드 확인 성공")
    void checkSignupEmailCode_Success() {
        // Given
        when(mailService.checkSignupEmailCode(any(String.class), any(String.class)))
                .thenReturn(true);

        // When
        ApiResponse<Boolean> response = mailController.checkSignupEmailCode(signupVerifyCodeDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }
}
