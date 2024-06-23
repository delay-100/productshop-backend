package com.whitedelay.productshop.security.config;

import com.whitedelay.productshop.security.UserDetails.UserDetailsServiceImpl;
import com.whitedelay.productshop.security.jwt.*;
import com.whitedelay.productshop.security.logout.CustomLogoutHandler;
import com.whitedelay.productshop.security.logout.CustomLogoutSuccessHandler;
import com.whitedelay.productshop.security.repository.TokenRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final TokenRepository tokenRepository;

    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
        this.tokenRepository = tokenRepository;
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
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, tokenRepository);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, tokenRepository);
    }

    @Bean
    public CustomLogoutHandler customLogoutHandler() {
        return new CustomLogoutHandler(tokenRepository, jwtUtil);
    }

    @Bean
    public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable());

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/signup/**").permitAll()
                        .anyRequest().authenticated()
        );

        http.formLogin((formLogin) ->
                formLogin
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error")
                        .permitAll()
        );

        http.logout((logout) ->
                logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(customLogoutHandler())
                        .logoutSuccessHandler(customLogoutSuccessHandler())
                        .permitAll()
        );

        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
