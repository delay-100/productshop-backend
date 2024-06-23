package com.whitedelay.productshop.security.repository;

import com.whitedelay.productshop.security.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByMemberidAndTokentypeAndExpiredFalse(String memberid, String tokentype);
    Optional<Token> findByTokenAndTokentype(String refreshToken, String refreshtokenHeader);
}
