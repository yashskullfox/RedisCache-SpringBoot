package com.search.mcp;

import com.search.CacheService;
import com.search.api.PatchData;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class AccountWriteTool implements McpToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(AccountWriteTool.class);

    private static final String ACCOUNT_AMOUNT_SCHEMA =
            "{\"type\":\"object\",\"properties\":{\"account\":{\"type\":\"string\"}," +
                    "\"amount\":{\"type\":\"number\"}},\"required\":[\"account\",\"amount\"]}";

    private static final String ACCOUNT_SCHEMA =
            "{\"type\":\"object\",\"properties\":{\"account\":{\"type\":\"string\"}}," +
                    "\"required\":[\"account\"]}";

    private final CacheService cacheService;

    public AccountWriteTool(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public void creditAccount(String account, long amount) throws Exception {
        log.info("tool=creditAccount account={} action=Credit timestamp={}", account, Instant.now());
        PatchData patch = new PatchData();
        patch.setAction("Credit");
        patch.setValue(amount);
        cacheService.updateDataInCache(parseAccount(account), patch);
    }

    public void withdrawAccount(String account, long amount) throws Exception {
        log.info("tool=withdrawAccount account={} action=Withdraw timestamp={}", account, Instant.now());
        PatchData patch = new PatchData();
        patch.setAction("Withdraw");
        patch.setValue(amount);
        cacheService.updateDataInCache(parseAccount(account), patch);
    }

    public void deleteAccount(String account) throws Exception {
        log.info("tool=deleteAccount account={} action=Delete timestamp={}", account, Instant.now());
        cacheService.removeDataFromCache(parseAccount(account));
    }

    @Override
    public List<McpServerFeatures.SyncToolSpecification> toolSpecs() {
        return List.of(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "creditAccount",
                                "Credit an amount to an account. Write operation — requires X-MCP-API-Key header.",
                                ACCOUNT_AMOUNT_SCHEMA),
                        this::handleCredit
                ),
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "withdrawAccount",
                                "Withdraw an amount from an account. Cannot go below zero. Write operation — requires X-MCP-API-Key header.",
                                ACCOUNT_AMOUNT_SCHEMA),
                        this::handleWithdraw
                ),
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "deleteAccount",
                                "Delete an account from the cache. Write operation — requires X-MCP-API-Key header.",
                                ACCOUNT_SCHEMA),
                        this::handleDelete
                )
        );
    }

    private McpSchema.CallToolResult handleCredit(McpSyncServerExchange exchange, Map<String, Object> args) {
        try {
            String account = (String) args.get("account");
            long amount = ((Number) args.get("amount")).longValue();
            creditAccount(account, amount);
            return new McpSchema.CallToolResult("credited " + amount + " to account " + account, false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(e.getMessage(), true);
        }
    }

    private McpSchema.CallToolResult handleWithdraw(McpSyncServerExchange exchange, Map<String, Object> args) {
        try {
            String account = (String) args.get("account");
            long amount = ((Number) args.get("amount")).longValue();
            withdrawAccount(account, amount);
            return new McpSchema.CallToolResult("withdrew " + amount + " from account " + account, false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(e.getMessage(), true);
        }
    }

    private McpSchema.CallToolResult handleDelete(McpSyncServerExchange exchange, Map<String, Object> args) {
        try {
            String account = (String) args.get("account");
            deleteAccount(account);
            return new McpSchema.CallToolResult("deleted account " + account, false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(e.getMessage(), true);
        }
    }

    private int parseAccount(String account) {
        try {
            return Integer.parseInt(account);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid account number: '" + account + "' — must be numeric");
        }
    }
}
