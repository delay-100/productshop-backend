package com.whitedelay.productshop.security.handler;

import com.whitedelay.productshop.security.entity.Token;
import com.whitedelay.productshop.security.jwt.JwtUtil;
import com.whitedelay.productshop.security.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${REFRESHTOKEN_HEADER}")
    public String REFRESHTOKEN_HEADER;

    @Value("${REFRESH_TOKEN_NAME}")
    private String refresh;


    public CustomLogoutHandler(TokenRepository tokenRepository, JwtUtil jwtUtil) {
        this.tokenRepository = tokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = jwtUtil.getTokenFromRequest(request, REFRESHTOKEN_HEADER);
        if (refreshToken != null) {
            Token token = tokenRepository.findByTokenAndTokentype(refreshToken, refresh)
                    .orElseThrow(() -> new IllegalArgumentException("Refreshtoken을 찾을 수 없습니다."));

            token.setExpired(true);
            tokenRepository.save(token);
        }
    }
}
