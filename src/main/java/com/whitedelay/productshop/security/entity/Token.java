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

    private String tokentype;
    private String token;
    private String memberid;
    private boolean expired;

    public Token(String tokentype, String token, String memberid, boolean expired) {
        this.tokentype = tokentype;
        this.token = token;
        this.memberid = memberid;
        this.expired = expired;
    }
}
