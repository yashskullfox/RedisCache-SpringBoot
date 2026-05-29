package com.search.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.SearchService;
import com.search.api.Request;
import com.search.api.Result;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AccountSearchTool implements McpToolAdapter {

    private static final String SEARCH_SCHEMA =
            "{\"type\":\"object\",\"properties\":{\"account\":{\"type\":\"string\"," +
                    "\"description\":\"Account number to search\"}},\"required\":[\"account\"]}";

    private static final String BALANCE_SCHEMA =
            "{\"type\":\"object\",\"properties\":{\"account\":{\"type\":\"string\"," +
                    "\"description\":\"Account number\"}},\"required\":[\"account\"]}";

    private final SearchService searchService;
    private final ObjectMapper objectMapper;

    public AccountSearchTool(SearchService searchService, ObjectMapper objectMapper) {
        this.searchService = searchService;
        this.objectMapper = objectMapper;
    }

    public Result searchAccount(String account) throws Exception {
        Request req = new Request();
        req.setAccount(account);
        return searchService.search(req);
    }

    public Long getAccountBalance(String account) throws Exception {
        Request req = new Request();
        req.setAccount(account);
        return searchService.search(req).getValue();
    }

    @Override
    public List<McpServerFeatures.SyncToolSpecification> toolSpecs() {
        return List.of(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "searchAccount",
                                "Search account cache by account number. Returns type, value, and last modification timestamp.",
                                SEARCH_SCHEMA),
                        this::handleSearchAccount
                ),
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "getAccountBalance",
                                "Get current balance for an account number from the cache.",
                                BALANCE_SCHEMA),
                        this::handleGetAccountBalance
                )
        );
    }

    private McpSchema.CallToolResult handleSearchAccount(McpSyncServerExchange exchange, Map<String, Object> args) {
        try {
            Result result = searchAccount((String) args.get("account"));
            return new McpSchema.CallToolResult(objectMapper.writeValueAsString(result), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(e.getMessage(), true);
        }
    }

    private McpSchema.CallToolResult handleGetAccountBalance(McpSyncServerExchange exchange, Map<String, Object> args) {
        try {
            Long balance = getAccountBalance((String) args.get("account"));
            return new McpSchema.CallToolResult(balance != null ? balance.toString() : "null", false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(e.getMessage(), true);
        }
    }
}
