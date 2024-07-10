package com.whitedelay.productshop.redis.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

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
        String lockName = "productOptionLock:" + productOptionId;
        RLock rLock = redissonClient.getLock(lockName);

        long waitTime = 5L;
        long leaseTime = 3L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        try {
            boolean available = rLock.tryLock(waitTime, leaseTime, timeUnit);
            if (!available) {
                throw new IllegalArgumentException("락 획득 불가");
            }

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
        } catch (InterruptedException e) {
            //락을 얻으려고 시도하다가 인터럽트를 받았을 때 발생하는 예외
            throw new IllegalArgumentException("인터럽트 발생");
        } finally {
            try {
                rLock.unlock();
                System.out.println("락 풀었음: " + rLock.getName());
            } catch (IllegalMonitorStateException e) {
                //이미 종료된 락일 때 발생하는 예외
                throw new IllegalArgumentException("락 풀기 에러");
            }
        }
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
