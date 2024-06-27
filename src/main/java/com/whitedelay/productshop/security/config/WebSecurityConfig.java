package com.whitedelay.productshop.security.config;

import com.whitedelay.productshop.security.UserDetails.UserDetailsServiceImpl;
import com.whitedelay.productshop.security.jwt.*;
//import com.whitedelay.productshop.security.handler.CustomLogoutHandler;
//import com.whitedelay.productshop.security.handler.CustomLogoutSuccessHandler;
//import com.whitedelay.productshop.security.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

//    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration, TokenRepository tokenRepository) {
    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
//        this.tokenRepository = tokenRepository;
    }

    // authenticationManager는 bean으로 직접 등록할거임
    // authenticationConfiguration 처럼 바로 가져올 수가 없어서 authenticationConfiguration으로 가져와서 수동등록함
    // 파라미터로 받아온 authenticationConfiguration을 넣어줘서 getAuthencationManager()을 가져옴
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 인증 필터 객체 생성 후 bean으로 등록
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
//        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, tokenRepository);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
//        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, tokenRepository);
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

//    @Bean
//    public CustomLogoutHandler customLogoutHandler() {
////        return new CustomLogoutHandler(tokenRepository, jwtUtil);
//        return new CustomLogoutHandler(jwtUtil);
//    }

//    @Bean
//    public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
//        return new CustomLogoutSuccessHandler();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf(AbstractHttpConfigurer::disable);

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/member/login/**").permitAll()
                        .requestMatchers("/signup/**").permitAll()
                        .requestMatchers("/refresh/**").permitAll()
                        .requestMatchers("/products/**").permitAll()
                        .anyRequest().authenticated()
        );
        // Exception Handling 설정 (access denied 처리)
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling
                        .authenticationEntryPoint(new FailedAuthenticationEntryPoint()) // 인증 실패 시 처리
        );

//        http.logout((logout) ->
//                logout
//                        .logoutUrl("/logout")
//                        .addLogoutHandler(customLogoutHandler())
//                        .logoutSuccessHandler(customLogoutSuccessHandler())
//                        .permitAll()
//        );

        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 인가 실패인 경우
    class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Authentication is required to access this resource.\"}");
        }
    }
}
