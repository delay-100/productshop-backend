package com.whitedelay.productshop.security.jwt;

import com.whitedelay.productshop.security.UserDetails.UserDetailsServiceImpl;
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

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter { // 기본 Filter 대신 받아온 Filter

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = jwtUtil.getTokenFromRequest(req); // 요청에서 jwt토큰 가져오는 메소드

        if (StringUtils.hasText(tokenValue)) { // jwt토큰이 있는지 없는지 확인하는 메소드
            // JWT 토큰 substring
            tokenValue = jwtUtil.substringToken(tokenValue); // 순수한 토큰만 떼어내기 위함('BEARER '떼기
            log.info(tokenValue);

            if (!jwtUtil.validateToken(tokenValue)) { // 토큰 검증
                log.error("Token Error");
                return;
            }

            Claims info = jwtUtil.getMemberInfoFromToken(tokenValue); // 토큰 유저 정보 가져오기

            try {
                 setAuthentication(info.getSubject()); // 토큰 만들때 setSubject로 uesr이름 넣었었음
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(req, res); // 여기까지 잘 실행되면, 다음 필터로 넘어감(DispatcherServlet를 통해 Controller로 갈 수 있음
    }

    // 인증 처리하는 메소드
    public void setAuthentication(String memberid) { // 파라미터: 유저 이름
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(memberid); // 인증 객체 생성하는 메소드 실행 후 인증 객체에 넣음
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String memberid) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(memberid); // userDetails를 뽑아오기 위한 메소드
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}