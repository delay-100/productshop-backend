package com.whitedelay.productshop.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Token extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tokenType;
    private String token;
    private String memberId;
    private boolean expired;

    public Token(String tokenType, String token, String memberId, boolean expired) {
        this.tokenType = tokenType;
        this.token = token;
        this.memberId = memberId;
        this.expired = expired;
    }
}
