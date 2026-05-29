package com.search.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class McpConfig {

    @Bean
    public WebMvcSseServerTransportProvider mcpTransportProvider(ObjectMapper objectMapper) {
        return new WebMvcSseServerTransportProvider(objectMapper, "/mcp/message", "/mcp/sse");
    }

    @Bean
    public RouterFunction<ServerResponse> mcpRouterFunction(WebMvcSseServerTransportProvider transportProvider) {
        return transportProvider.getRouterFunction();
    }

    @Bean(destroyMethod = "close")
    public McpSyncServer mcpSyncServer(WebMvcSseServerTransportProvider transportProvider,
                                       List<McpToolAdapter> adapters) {
        List<McpServerFeatures.SyncToolSpecification> allTools = adapters.stream()
                .flatMap(a -> a.toolSpecs().stream())
                .collect(Collectors.toList());

        return McpServer.sync(transportProvider)
                .serverInfo("account-search-service", "1.2.0")
                // tools(false) = tool list is static; no tools/list_changed notifications sent.
                // Change to tools(true) and call mcpServer.notifyToolsListChanged() if tools become dynamic.
                .capabilities(McpSchema.ServerCapabilities.builder().tools(false).build())
                .tools(allTools)
                .build();
    }
}
