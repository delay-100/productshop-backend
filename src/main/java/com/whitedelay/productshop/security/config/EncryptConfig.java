package com.whitedelay.productshop.security.config;

import com.whitedelay.productshop.util.AES256Encoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class EncryptConfig {

    @Bean
    public AES256Encoder aes256Encoder() {
        return new AES256Encoder();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
