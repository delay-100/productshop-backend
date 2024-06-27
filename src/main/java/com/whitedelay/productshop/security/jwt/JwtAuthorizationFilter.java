package com.whitedelay.productshop.security.jwt;

import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import com.whitedelay.productshop.security.UserDetails.UserDetailsServiceImpl;
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

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${ACCESS_TOKEN_NAME}")
    private String access;

    @Value("${REFRESH_TOKEN_NAME}")
    private String refresh;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.getTokenFromRequest(req);

        log.info("AccessToken: {}", accessToken);

        if (StringUtils.hasText(accessToken)) {
            accessToken = jwtUtil.substringToken(accessToken);
            log.info("SubStringToken: {}", accessToken);
            if (!jwtUtil.validateToken(accessToken)) {
                log.error("Token Error");
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
            Claims info = jwtUtil.getMemberInfoFromToken(accessToken);
            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

        private void setAuthentication(String memberId) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication = createAuthentication(memberId);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        private Authentication createAuthentication(String memberId) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }

}
