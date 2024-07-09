package com.whitedelay.productshop.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void setInitialStock(Long productOptionId, int stock) {
        redisTemplate.opsForValue().set("productOption:" + productOptionId, String.valueOf(stock));
    }

    public int getStock(Long productOptionId) {
        return Integer.parseInt(redisTemplate.opsForValue().get("productOption:" + productOptionId));
    }

    public void setStock(Long productOptionId, int stock) {
        redisTemplate.opsForValue().set("productOption:" + productOptionId, String.valueOf(stock));
    }

    public boolean deductStock(Long productOptionId, int quantity) {
        String stockStr = redisTemplate.opsForValue().get("productOption:" + productOptionId);
        if (stockStr == null) {
            throw new IllegalArgumentException("상품 옵션의 재고 정보가 없습니다.");
        }

        int stock = Integer.parseInt(stockStr);
        if (stock < quantity) {
            return false;
        }

        redisTemplate.opsForValue().set("productOption:" + productOptionId, String.valueOf(stock - quantity));
        return true;
    }

    public void restoreStock(Long productOptionId, int quantity) {
        String key = "productOption:" + productOptionId;
        redisTemplate.opsForValue().increment(key, quantity);
    }

    public void clearAllProductOptions() {
        Set<String> keys = redisTemplate.keys("productOption:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
