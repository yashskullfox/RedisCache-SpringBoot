package com.search.mcp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class McpRateLimiter {

    private final int writesPerMinute;
    private final ConcurrentHashMap<String, AtomicLong> buckets = new ConcurrentHashMap<>();

    public McpRateLimiter(@Value("${mcp.rate-limit.writes-per-minute:10}") int writesPerMinute) {
        this.writesPerMinute = writesPerMinute;
    }

    public boolean tryConsume(String clientIp) {
        if (writesPerMinute <= 0) {
            return true;
        }
        String key = (clientIp == null || clientIp.isBlank()) ? "unknown" : clientIp;
        AtomicLong tokens = buckets.computeIfAbsent(key, ignored -> new AtomicLong(writesPerMinute));

        while (true) {
            long current = tokens.get();
            if (current <= 0) {
                return false;
            }
            if (tokens.compareAndSet(current, current - 1)) {
                return true;
            }
        }
    }

    @Scheduled(fixedRate = 60_000)
    public void refillBuckets() {
        if (writesPerMinute <= 0) {
            return;
        }
        buckets.forEach((ip, bucket) -> bucket.set(writesPerMinute));
    }
}
