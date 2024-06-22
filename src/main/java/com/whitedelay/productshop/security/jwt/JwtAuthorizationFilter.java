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
    private static final String access = "Access";
    private static final String refresh = "Refresh";

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
            log.info("Access token: " + accessToken); // accesstoken에서 'Bearer을 뗀 값 출력

            if (!jwtUtil.validateToken(accessToken)) { // accesstoken이 만료된 경우
                if (StringUtils.hasText(refreshToken)) {
                    refreshToken = jwtUtil.substringToken(refreshToken); // refreshToken에서 'Bearer ' 을 뗌

                    if (jwtUtil.validateToken(refreshToken)) {
                        String memberId = jwtUtil.getMemberInfoFromToken(refreshToken).getSubject();
                        log.info("Member ID from refresh token: " + memberId);

                        Optional<Token> refreshTokenEntity = tokenRepository.findByMemberidAndTokentypeAndExpiredFalse(memberId, refresh);
                        log.info("Refresh token from database.substringToken: " + jwtUtil.substringToken(refreshTokenEntity.get().getToken()));
                        log.info("refreshTOken: " + refreshToken);
                        if (refreshToken.equals(jwtUtil.substringToken(refreshTokenEntity.get().getToken()))) {
                            System.out.println("access 토큰 재발급");
                            String newAccessToken = jwtUtil.createToken(memberId, access, MemberRoleEnum.USER);
                            jwtUtil.addJwtToCookie(newAccessToken, access, res);
                            accessToken = jwtUtil.substringToken(newAccessToken);
                            System.out.println("newAccessToken = " + newAccessToken);
                        } else {
                            System.out.println("refreshtoken이랑 db의 refreshtoken이 다름");
                        }
                    } else {// refresh 토큰이 만료된 경우
                        log.error("Refresh Token Error: Token invalid");
                        System.out.println("Refresh 토큰 만료 ");
                        log.info("refreshToken: " + refreshToken); // accesstoken에서 'Bearer을 뗀 값 출력
                        refreshToken = "Bearer " + refreshToken;
                        log.info("Plus Bearer refreshToken: " + refreshToken); // accesstoken에서 'Bearer을 뗀 값 출력

                        Token expiredRefreshToken = tokenRepository.findByToken(refreshToken)
                                .orElseThrow(() -> new IllegalArgumentException("찾는 토큰이 없습니다."));

                        expiredRefreshToken.setExpired(true);
                        tokenRepository.save(expiredRefreshToken);

                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                }
            }
            System.out.println("accessToken = " + accessToken);
            Claims info = jwtUtil.getMemberInfoFromToken(accessToken);
            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

    public void setAuthentication(String memberid) {
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
