package com.search.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.CacheService;
import com.search.api.CacheData;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("build")
class McpIntegrationIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    CacheService cacheService;

    private McpSyncClient mcpClient;

    @BeforeEach
    void setUpMcpClient() {
        HttpClientSseClientTransport transport = new HttpClientSseClientTransport(
                HttpClient.newBuilder(),
                "http://localhost:" + port,
                "/mcp/sse",
                new ObjectMapper()
        );
        mcpClient = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(30))
                .build();
        mcpClient.initialize();
    }

    @AfterEach
    void tearDownMcpClient() {
        if (mcpClient != null) {
            mcpClient.close();
        }
    }

    @Test
    void actuatorInfo_listsBothReadTools() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/info", String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("searchAccount").contains("getAccountBalance");
    }

    @Test
    void mcpListTools_returnsBothReadTools() {
        McpSchema.ListToolsResult result = mcpClient.listTools();

        assertThat(result.tools())
                .extracting(McpSchema.Tool::name)
                .contains("searchAccount", "getAccountBalance");
    }

    @Test
    void mcpCallSearchAccount_cacheMiss_returnsZeroedResult() {
        McpSchema.CallToolResult result = mcpClient.callTool(
                new McpSchema.CallToolRequest("searchAccount", Map.of("account", "00000")));

        assertThat(result.isError()).isFalse();
        assertThat(result.content()).isNotEmpty();
        String text = ((McpSchema.TextContent) result.content().get(0)).text();
        assertThat(text).contains("\"account\"");
    }

    @Test
    void mcpCallSearchAccount_cacheHit_returnsResult() throws Exception {
        CacheData data = new CacheData();
        data.setType("checking");
        data.setValue("5000");
        cacheService.addDataToCache(55001, data);

        McpSchema.CallToolResult result = mcpClient.callTool(
                new McpSchema.CallToolRequest("searchAccount", Map.of("account", "55001")));

        assertThat(result.isError()).isFalse();
        String text = ((McpSchema.TextContent) result.content().get(0)).text();
        assertThat(text).contains("\"account\"").contains("55001");
    }

    @Test
    void mcpCallGetAccountBalance_cacheHit_returnsBalance() throws Exception {
        CacheData data = new CacheData();
        data.setType("savings");
        data.setValue("9000");
        cacheService.addDataToCache(55002, data);

        McpSchema.CallToolResult result = mcpClient.callTool(
                new McpSchema.CallToolRequest("getAccountBalance", Map.of("account", "55002")));

        assertThat(result.isError()).isFalse();
        String text = ((McpSchema.TextContent) result.content().get(0)).text();
        assertThat(text).isEqualTo("9000");
    }
}
