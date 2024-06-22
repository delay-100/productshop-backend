package com.whitedelay.productshop.security.jwt;

import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import com.whitedelay.productshop.security.UserDetails.UserDetailsServiceImpl;
import com.whitedelay.productshop.security.entity.Token;
import com.whitedelay.productshop.security.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenRepository tokenRepository;

    @Value("${ACCESS_TOKEN_NAME}")
    private String access;

    @Value("${REFRESH_TOKEN_NAME}")
    private String refresh;


    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.getTokenFromRequest(req, access);
        String refreshToken = jwtUtil.getTokenFromRequest(req, refresh);

        if (StringUtils.hasText(accessToken)) {
            accessToken = jwtUtil.substringToken(accessToken);
            if (!jwtUtil.validateToken(accessToken) && StringUtils.hasText(refreshToken)) {
                refreshToken = jwtUtil.substringToken(refreshToken);

                if (jwtUtil.validateToken(refreshToken)) {
                    String memberIdFromAccessToken = jwtUtil.getMemberInfoFromToken(accessToken).getSubject();

                    Optional<Token> refreshTokenEntity = tokenRepository.findByMemberidAndTokentypeAndExpiredFalse(memberIdFromAccessToken, refresh);
                    if (refreshToken.equals(jwtUtil.substringToken(refreshTokenEntity.get().getToken()))) {
                        String newAccessToken = jwtUtil.createToken(memberIdFromAccessToken, access, MemberRoleEnum.USER);
                        jwtUtil.addJwtToCookie(newAccessToken, access, res);
                        accessToken = jwtUtil.substringToken(newAccessToken);

                    } else {
                        // 현재 요청을 보낸 사용자랑 refresh token의 주인이 다른 경우
                        handleExpiredRefreshToken(refreshTokenEntity, res);
                        return;
                    }
                } else { // refresh token의 유효기간이 만료된 경우
                    String memberIdFromAccessToken = jwtUtil.getMemberInfoFromToken(accessToken).getSubject();
                    Optional<Token> refreshTokenEntity = tokenRepository.findByMemberidAndTokentypeAndExpiredFalse(memberIdFromAccessToken, refresh);

                    handleExpiredRefreshToken(refreshTokenEntity, res);
                    return;
                }
            }

            if (jwtUtil.validateToken(accessToken)) {
                Claims info = jwtUtil.getMemberInfoFromToken(accessToken);
                setAuthentication(info.getSubject());
            }
        }

        filterChain.doFilter(req, res);
    }

        private void handleExpiredRefreshToken(Optional<Token> refreshTokenEntity, HttpServletResponse res) throws IOException {
            if (refreshTokenEntity.isPresent()) {
                Token expiredRefreshToken = refreshTokenEntity.get();
                expiredRefreshToken.setExpired(true);
                tokenRepository.save(expiredRefreshToken);
            }
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Authentication failed: Refresh token expired");
        }

        private void setAuthentication(String memberid) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication = createAuthentication(memberid);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        private Authentication createAuthentication(String memberid) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(memberid);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }

}
