//package com.whitedelay.productshop.security.handler;
//
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
//    @Value("${AUTHORIZATION_HEADER}")
//    public String AUTHORIZATION_HEADER;
//
//    @Value("${REFRESHTOKEN_HEADER}")
//    public String REFRESHTOKEN_HEADER;
//
//    @Override
//    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        clearCookies(response);
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.getWriter().write("Logout successful~~");
//    }
//
//    private void clearCookies(HttpServletResponse response) {
//        Cookie accessTokenCookie = new Cookie(AUTHORIZATION_HEADER, null);
//        accessTokenCookie.setPath("/");
//        accessTokenCookie.setHttpOnly(true);
//        accessTokenCookie.setMaxAge(0);
//
//        Cookie refreshTokenCookie = new Cookie(REFRESHTOKEN_HEADER, null);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setMaxAge(0);
//
//        response.addCookie(accessTokenCookie);
//        response.addCookie(refreshTokenCookie);
//    }
//}
