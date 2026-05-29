package com.search.mcp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class McpAuthFilterTest {

    private McpAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new McpAuthFilter("secret-key", new McpRateLimiter(10));
    }

    @Test
    void missingApiKey_returns401() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/mcp/message");
        req.addHeader("Accept-MCP-Version", "1");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, new MockFilterChain());

        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    void wrongApiKey_returns401() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/mcp/message");
        req.addHeader("Accept-MCP-Version", "1");
        req.addHeader("X-MCP-API-Key", "wrong-key");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, new MockFilterChain());

        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    void validApiKey_requestProceeds() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/mcp/message");
        req.addHeader("Accept-MCP-Version", "1");
        req.addHeader("X-MCP-API-Key", "secret-key");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(200);
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    void unsupportedVersion_returns400() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/mcp/message");
        req.addHeader("Accept-MCP-Version", "99");
        req.addHeader("X-MCP-API-Key", "secret-key");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, new MockFilterChain());

        assertThat(res.getStatus()).isEqualTo(400);
        assertThat(res.getContentAsString()).contains("unsupported_version");
    }

    @Test
    void missingVersionHeader_defaultsToV1AndProceeds() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/mcp/message");
        req.addHeader("X-MCP-API-Key", "secret-key");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(200);
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    void responseContainsVersionHeader() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/mcp/message");
        req.addHeader("Accept-MCP-Version", "1");
        req.addHeader("X-MCP-API-Key", "secret-key");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, new MockFilterChain());

        assertThat(res.getHeader("x-mcp-version")).isEqualTo("1");
    }

    @Test
    void noAuthRequired_whenKeyNotConfigured() throws Exception {
        McpAuthFilter noAuthFilter = new McpAuthFilter("", new McpRateLimiter(10));
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/mcp/message");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        noAuthFilter.doFilter(req, res, chain);

        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    void nonMcpPath_isNotFiltered() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/actuator/health");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    void eleventhWriteCall_returns429() throws Exception {
        McpAuthFilter limitedFilter = new McpAuthFilter("secret-key", new McpRateLimiter(10));

        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("POST", "/mcp/message");
            req.setRemoteAddr("10.10.10.10");
            req.addHeader("Accept-MCP-Version", "1");
            req.addHeader("X-MCP-API-Key", "secret-key");
            MockHttpServletResponse res = new MockHttpServletResponse();
            limitedFilter.doFilter(req, res, new MockFilterChain());
            assertThat(res.getStatus()).isEqualTo(200);
        }

        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/mcp/message");
        req.setRemoteAddr("10.10.10.10");
        req.addHeader("Accept-MCP-Version", "1");
        req.addHeader("X-MCP-API-Key", "secret-key");
        MockHttpServletResponse res = new MockHttpServletResponse();

        limitedFilter.doFilter(req, res, new MockFilterChain());

        assertThat(res.getStatus()).isEqualTo(429);
        assertThat(res.getContentAsString()).contains("rate_limit_exceeded");
    }
}
