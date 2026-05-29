package com.search.mcp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class McpAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(McpAuthFilter.class);
    private static final List<String> SUPPORTED_VERSIONS = List.of("1");

    private final String configuredApiKey;
    private final McpRateLimiter rateLimiter;

    public McpAuthFilter(@Value("${mcp.auth.api-key:}") String configuredApiKey, McpRateLimiter rateLimiter) {
        this.configuredApiKey = configuredApiKey;
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/mcp/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        response.setHeader("x-mcp-version", "1");

        String requestedVersion = request.getHeader("Accept-MCP-Version");
        if (requestedVersion == null) {
            log.warn("Accept-MCP-Version header missing — defaulting to v1 (pre-versioning request)");
        } else if (!SUPPORTED_VERSIONS.contains(requestedVersion)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"unsupported_version\",\"supported\":[\"1\"]}");
            return;
        }

        if (StringUtils.isNotBlank(configuredApiKey)) {
            String providedKey = request.getHeader("X-MCP-API-Key");
            if (!configuredApiKey.equals(providedKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        if (StringUtils.isNotBlank(configuredApiKey)
                && "POST".equalsIgnoreCase(request.getMethod())
                && "/mcp/message".equals(request.getRequestURI())
                && !rateLimiter.tryConsume(request.getRemoteAddr())) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"rate_limit_exceeded\",\"retry_after_seconds\":60}");
            return;
        }

        chain.doFilter(request, response);
    }
}
