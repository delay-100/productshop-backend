package com.whitedelay.productshop.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whitedelay.productshop.member.entity.MemberRoleEnum;

import com.whitedelay.productshop.security.UserDetails.UserDetailsImpl;
import com.whitedelay.productshop.member.dto.LoginRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성") // 필터에서 인증, 인가를 다 처리할거임
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter { // UsernamePasswordAuthenticationFilter을 상속받아옴, 세션 방식 말고 jwt쓸거라서 custom하는거임

    private final JwtUtil jwtUtil; // jwtutil을 생성자 주입으로 가져옴

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
//        this.tokenRepository = tokenRepository;
// url 인터셉트
//        setFilterProcessesUrl("/login"); // UsernamePasswordAuthenticationFilter를 상속받으면 호출할 수 있는 메소드임
                                                  // 기존에는  WebSecurityConfig에서  // 로그인 처리 (POST /api/user/login)  .loginProcessingUrl("/api/user/login")  이렇게 해줬었음
        // 커스텀할거라 여기서 set메소드로 할거임
    }

    @Override // 로그인 시도하는 메소드
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도"); 
        try { // ObjectMapper(데이터, 변환할 타입) : json형태의 String 데이터를 Object로 바꾸는거
            // request.getInputStream() : 현재 HttpServletRequest의 body부분에 username이랑 password가 json형식으로 넘어오기 때문에 해당 데이터를 가져오는 거임
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate( // WebSecurityConfig에서 setAuthenticationManger을 해줘서 사용 가능
                                                            // UsernamePasswordAuthenticationFilter를 상속받아서 사용가능한 메소드
                                                            // authenticate: 인증 처리하는 메소드
                    new UsernamePasswordAuthenticationToken( // UsernamePasswordAuthenticationFilter동작에서 초반에 getAuthenticationManager한테 UsernamePasswordAuthenticationToken을 전해줘야 함
                            requestDto.getMemberId(), // username(아이디)
                            requestDto.getPassword(), // password(비밀번호)
                            null // 권한: 인증처리할때 지금 필요가 없기때문에 null을 줌
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override // 로그인(인증)성공 시 실행되는 메소드
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성"); // 로그인이 성공(id, pw가 맞으면)하면 jwt를 생성해줘야함
        // 이 메소드는 Authentication 인증 객체를 받아오는데, 이 안에 UserDetails가 들어있음 UserDetails랑 UserDetailsService를 AuthenticationManger가 사용함
        String memberId = ((UserDetailsImpl) authResult.getPrincipal()).getUsername(); // /api/products에서 @AuthenticationPrinciple로 UserDetails를 받아올 수 있었음, 그걸 코드로 작성한거임이거는
        MemberRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getMember().getRole();

        String accessToken = jwtUtil.createAccessToken(memberId, role); // 토큰 생성 시 2번째 파라미터로 role을 넣어주기 위함

        jwtUtil.addJwtToCookie(accessToken, response); // 쿠키생성하고 token을 넣어주는 메소드
        // 이후에 response객체에 자동으로 담김

    }

     // 로그인 실패 시 실행되는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(401);
        response.getWriter().write("Authentication failed: " + failed.getMessage());
    }

}