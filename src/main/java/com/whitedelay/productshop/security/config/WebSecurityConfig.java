package com.whitedelay.productshop.security.config;

import com.whitedelay.productshop.security.UserDetails.UserDetailsServiceImpl;
import com.whitedelay.productshop.security.jwt.JwtAuthenticationFilter;
import com.whitedelay.productshop.security.jwt.JwtAuthorizationFilter;
import com.whitedelay.productshop.security.jwt.JwtUtil;
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
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration)); // JwtAuthenticationFilter에서 attemptAuthentication 메소드 사용 시 authenticationManager을 필요로 함
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, tokenRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers("/login/**").permitAll() // '/login/'로 시작하는 요청 모두 접근 허가
                        .requestMatchers("/signup/**").permitAll() // '/signup/'로 시작하는 요청 모두 접근 허가
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

       // 로그인 사용
        http.formLogin((formLogin) ->
                formLogin
//////                        .loginPage("/login") // 로그인 view (GET /login) -> 프론트가 없으므로 미구현
                        .loginProcessingUrl("/login") // 로그인 처리 (POST /login)
                        .defaultSuccessUrl("/") // 로그인 성공 시 이동할 url -> 메인으로 이동
                        .failureUrl("/login?error") // 로그인 실패 시 이동할 url -> 프론트가 없으므로 로그인 실패로 메세지 보내는 get요청 실행예정
                        .permitAll()
        );
        // 필터 관리, 위에서 filter을 만들고 어떤 순서에 어디에 끼워넣을건지 적는거임
        // 인가를 먼저 진행 -> 인가가 되지 않았으면 로그인을 진행(Authorization이 앞에있는 이유)
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class); // JwtAuthenticationFilter가 수행되기 전에 jwtAuthorizationFilter 수행
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // UsernamePasswordAuthenticationFilter.class가 수행되기 전에 jwtAuthenticationFilter를 실행


        return http.build();
    }

}
