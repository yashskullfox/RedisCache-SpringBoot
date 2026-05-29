package com.search.mcp;

import com.search.CacheService;
import com.search.SearchService;
import com.search.api.CacheData;
import com.search.api.Request;
import com.search.api.Result;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("build")
class AccountWriteToolTest {

    private static final int ACCOUNT = 77001;
    private static final String ACCOUNT_STR = "77001";
    @Autowired
    AccountWriteTool writeTool;
    @Autowired
    CacheService cacheService;
    @Autowired
    SearchService searchService;

    @BeforeEach
    void setup() throws Exception {
        cacheService.removeDataFromCache(ACCOUNT);
        CacheData data = new CacheData();
        data.setType("savings");
        data.setValue("1000");
        cacheService.addDataToCache(ACCOUNT, data);
    }

    @Test
    void creditAccount_increasesBalance() throws Exception {
        writeTool.creditAccount(ACCOUNT_STR, 500L);

        Result result = searchService.search(request(ACCOUNT_STR));

        assertThat(result.getValue()).isEqualTo(1500L);
    }

    @Test
    void withdrawAccount_decreasesBalance() throws Exception {
        writeTool.withdrawAccount(ACCOUNT_STR, 200L);

        Result result = searchService.search(request(ACCOUNT_STR));

        assertThat(result.getValue()).isEqualTo(800L);
    }

    @Test
    void withdrawAccount_doesNotGoBelowZero() throws Exception {
        writeTool.withdrawAccount(ACCOUNT_STR, 5000L);

        Result result = searchService.search(request(ACCOUNT_STR));

        assertThat(result.getValue()).isEqualTo(1000L);
    }

    @Test
    void deleteAccount_evictsKey() throws Exception {
        writeTool.deleteAccount(ACCOUNT_STR);

        Result result = searchService.search(request(ACCOUNT_STR));

        assertThat(result.getValue()).isNull();
        assertThat(result.getType()).isNull();
    }

    @Test
    void handleCredit_invalidAccount_returnsHelpfulError() throws Exception {
        Method handler = AccountWriteTool.class.getDeclaredMethod(
                "handleCredit", McpSyncServerExchange.class, Map.class);
        handler.setAccessible(true);

        McpSchema.CallToolResult result = (McpSchema.CallToolResult) handler.invoke(
                writeTool, null, Map.of("account", "not-a-number", "amount", 100));

        assertThat(result.isError()).isTrue();
        String text = ((McpSchema.TextContent) result.content().get(0)).text();
        assertThat(text).contains("Invalid account number");
    }

    private Request request(String account) {
        Request req = new Request();
        req.setAccount(account);
        return req;
    }
}
