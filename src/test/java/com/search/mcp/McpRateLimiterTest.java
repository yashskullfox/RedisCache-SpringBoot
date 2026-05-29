package com.search.mcp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class McpRateLimiterTest {

    @Test
    void limitTwo_allowsTwoThenDeniesThird() {
        McpRateLimiter limiter = new McpRateLimiter(2);

        assertThat(limiter.tryConsume("1.1.1.1")).isTrue();
        assertThat(limiter.tryConsume("1.1.1.1")).isTrue();
        assertThat(limiter.tryConsume("1.1.1.1")).isFalse();
    }

    @Test
    void refillTick_replenishesTokens() {
        McpRateLimiter limiter = new McpRateLimiter(2);

        limiter.tryConsume("2.2.2.2");
        limiter.tryConsume("2.2.2.2");
        assertThat(limiter.tryConsume("2.2.2.2")).isFalse();

        limiter.refillBuckets();

        assertThat(limiter.tryConsume("2.2.2.2")).isTrue();
    }
}
