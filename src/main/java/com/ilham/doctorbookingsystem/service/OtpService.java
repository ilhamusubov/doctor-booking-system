package com.ilham.doctorbookingsystem.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class OtpService {

    private final StringRedisTemplate redisTemplate;

    public OtpService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOTP(String email, String otp) {
        redisTemplate.opsForValue()
                .set("otp:" + email, otp, Duration.ofMinutes(5));
    }

    public String getOTP(String email) {
        return redisTemplate.opsForValue().get("otp:" + email);
    }

    public void deleteOTP(String email) {
        redisTemplate.delete("otp:" + email);
    }
}
