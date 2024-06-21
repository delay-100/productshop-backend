package com.whitedelay.productshop.member.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String VERIFICATION_CODE_KEY_PREFIX = "verification-code:";
    private static final String VERIFICATION_CODE_KEY_CHECK = "_checked";
    public String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 9000) + 1000);
    }

    public boolean saveVerificationCode(String email, String verificationCode) {
        String key = VERIFICATION_CODE_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, verificationCode, 10, TimeUnit.MINUTES);
        return true;
    }

    public boolean verifyEmail(String email, String verificationCode) {
        String key = VERIFICATION_CODE_KEY_PREFIX + email;
        String savedCode = redisTemplate.opsForValue().get(key);
        System.out.println("key = " + key);
        System.out.println("savedCode = " + savedCode);
        if (savedCode != null && savedCode.equals(verificationCode)) {
            redisTemplate.opsForValue().set(key, verificationCode + VERIFICATION_CODE_KEY_CHECK, 10, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }
    public boolean isEmailVerified(String email) {
        String key = VERIFICATION_CODE_KEY_PREFIX + email;
        String value = redisTemplate.opsForValue().get(key);
        System.out.println("email = " + email);
        System.out.println("key = " + key);
        System.out.println("value = " + value);
        return value != null && value.endsWith(VERIFICATION_CODE_KEY_CHECK);
    }
}
