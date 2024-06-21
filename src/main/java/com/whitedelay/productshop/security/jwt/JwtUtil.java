package com.whitedelay.productshop.security.jwt;

import com.whitedelay.productshop.member.entity.MemberRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
// JWT 관련 기능들을 가진 JwtUtil이라는 클래스를 만들어 JWT 관련 기능을 수행시킴
//<JWT 관련 기능>
//1. JWT 생성 -> 생성한 토큰을 반환하는 방법 2가지(1.그냥 헤더에 담아 보냄(Response객체의 header에 그냥 token넣어 보내기) 2. Cookie객체에 Response에 담는 방법(cookie.setToken해서 넣고 Response객체에 넣어 보내기))
//2. 생성된 JWT를 Cookie에 저장
//3. Cookie에 들어있던 JWT 토큰을 Substring
//4. JWT 검증
//5. JWT에서 사용자 정보 가져오기
@Component
public class JwtUtil { // util 클래스: 다른 객체에 의존하지 않고 하나의 모듈로서 동작하는 클래스
    // Header KEY 값(cookie에서 저장할 이름)
    public static final String AUTHORIZATION_HEADER = "Authorization"; 
    // 사용자 권한 값의 KEY(admin, user) -> 비밀번호와 같이 보안이 중요하지는 않아서 jwt내에 보내기도함
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자(Token네이밍 규칙)
    public static final String BEARER_PREFIX = "Bearer "; // 토큰 앞에 구분하기 위해 한칸 띄워야 함
    // 토큰 만료시간(expired_time으로 해도 됨)
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 임의로 60분으로 설정(토큰의 유지 시간) 기본은 1ms(1초)

    // @Value는 Beansfactory에서 가져옴(위에 import확인)
    @Value("${JWT_SECRET_KEY}") // Base64 Encode 한 SecretKey
    private String secretKey; //  Encode된 Secret Key를 Decode 해서 사용
    private Key key; // Decode된 Secret Key를 담는 객체
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정, "로깅", 애플리케이션이 동작될 때 시간순으로 기록하는 것임_기본적으로 가지고 있어서 사용할 수 있음
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그"); // Logback 로깅 프레임워크

    @PostConstruct // 딱 한번만 받아와야하는 값을 받아올때 요청을 새로 호출하는 실수를 방지하기 위한 어노테이션
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey); // 디코딩하여 byte배열로 받아옴
        key = Keys.hmacShaKeyFor(bytes); // hmacShaKeyFor메소드에서 변환이 일어나고, key를 뱉어줌
    }

    // 1. JWT 생성 -> 생성한 토큰을 반환하는 방법 2가지(1.그냥 헤더에 담아 보냄(Response객체의 header에 그냥 token넣어 보내기) 2. Cookie객체에 Response에 담는 방법(cookie.setToken해서 넣고 Response객체에 넣어 보내기))
    // 토큰 생성
    public String createToken(String memberid, MemberRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX + // 'BEARER '
                Jwts.builder()
                        .setSubject(memberid) // 사용자 식별자값(ID), ID를 넣어도 상관없는데 여기서는 username을 넣음
                        .claim(AUTHORIZATION_KEY, role) // jwt사용자의 권한 정보를 넣음, UserRole의 enum정보를 넣음, claim은 key, value로 데이터를 넣는 것
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘(시크릿 키, 시크릿 알고리즘)을 넣어주면 됨
                        .compact(); // jwt옵션을 전부다 넣지 않아도 되는데 여기서는 이것저것 해보려고 많이 넣은거임
    }
    // 2. 생성된 JWT를 Cookie에 저장
    // JWT Cookie 에 저장
    public void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value(encoding한 토큰 값을 넣음)
            cookie.setPath("/");

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }
    //3. Cookie에 들어있던 JWT 토큰을 Substring(BEARER 떼기
    // JWT 토큰 substring
    public String substringToken(String tokenValue) {
        // 순수한 토큰 값을 추출
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) { // 1. 공백과 NULL 검증(아니어야하니까), 2. token이 'BEARER '로 시작하는지 확인
            return tokenValue.substring(7); // 'BEARER '이 7자임
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }
    //4. JWT 검증
    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); // 토큰 검증, key: secretKey, token: 가져온 토큰
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 검증해서 문제가 없음이 확인됨
    //5. JWT에서 사용자 정보 가져오기
    // 토큰에서 사용자 정보 가져오기
    public Claims getMemberInfoFromToken(String token) { // jwt가 Claim기반임
        // 데이터를 가져오려면 secretKey값이 필요(setSigningKey)
        // 분석을 할 토큰을 넣어줘야 함(parseClaimsJws)
        // Body안에 들어있는 Claims를 가져올 수 있음(Claims 안에 있는 사용자 정보를 가져올 수 있음)
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }


    // @CookieValue를 사용할 수 없는 경우에
    // HttpServletRequest 에서 Cookie Value : JWT 가져오기
    public String getTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}