package com.whitedelay.productshop.member.controller;

import com.whitedelay.productshop.member.dto.*;
import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import com.whitedelay.productshop.util.ApiResponse;
import com.whitedelay.productshop.member.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
    }

    @Test
    @DisplayName("회원가입")
    public void Signup_Success() {
        // Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .memberId("testUser")
                .password("password123")
                .email("test@example.com")
                .memberName("Test User")
                .address("123 Test St")
                .zipCode(12345)
                .phone("010-1234-5678")
                .role(MemberRoleEnum.USER)
                .emailCode("12345678")
                .build();
        when(authService.signup(any(SignupRequestDto.class))).thenReturn(true);

        // When
        ApiResponse<Boolean> response = authController.signup(signupRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    @DisplayName("로그인")
    public void Login_Success() {
        // Given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .memberId("testUser")
                .password("password123")
                .build();

        LoginResponseDto loginResponseDto = LoginResponseDto.from("testUser", "refreshToken123");
        when(authService.login(any(LoginRequestDto.class), any(HttpServletResponse.class))).thenReturn(loginResponseDto);

        // When
        ApiResponse<LoginResponseDto> response = authController.login(loginRequestDto, this.response);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isEqualTo(loginResponseDto);
    }

    @Test
    @DisplayName("RefreshToken 재발급")
    public void RefreshToken_Success() {
        // Given
        RefreshTokenRequestDto refreshTokenRequestDto = RefreshTokenRequestDto.builder()
                .memberId("testUser")
                .refreshToken("refreshToken123")
                .build();
        RefreshTokenResponseDto refreshTokenResponseDto = RefreshTokenResponseDto.from("testUser", "newAccessToken123");
        when(authService.refreshToken(any(String.class), any(String.class), any(HttpServletResponse.class)))
                .thenReturn(refreshTokenResponseDto);

        // When
        ApiResponse<RefreshTokenResponseDto> response = authController.refreshToken(refreshTokenRequestDto, this.response);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isEqualTo(refreshTokenResponseDto);
    }
}
