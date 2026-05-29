package com.search.mcp;

import com.search.SearchService;
import com.search.api.Request;
import com.search.api.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("build")
class AccountSearchToolTest {

    @Autowired
    AccountSearchTool tool;

    @MockitoBean
    SearchService searchService;

    @Test
    void searchAccount_hit_returnsResult() throws Exception {
        Result expected = new Result();
        expected.setAccount(12345);
        expected.setType("savings");
        expected.setValue(1000L);
        expected.setLastModification(Instant.now());
        when(searchService.search(any(Request.class))).thenReturn(expected);

        Result actual = tool.searchAccount("12345");

        assertThat(actual.getAccount()).isEqualTo(12345);
        assertThat(actual.getType()).isEqualTo("savings");
        assertThat(actual.getValue()).isEqualTo(1000L);
    }

    @Test
    void searchAccount_miss_returnsEmptyResult() throws Exception {
        when(searchService.search(any(Request.class))).thenReturn(new Result());

        Result actual = tool.searchAccount("99999");

        assertThat(actual.getAccount()).isZero();
        assertThat(actual.getValue()).isNull();
        assertThat(actual.getType()).isNull();
    }

    @Test
    void getAccountBalance_hit_returnsValue() throws Exception {
        Result result = new Result();
        result.setValue(2500L);
        when(searchService.search(any(Request.class))).thenReturn(result);

        Long balance = tool.getAccountBalance("12345");

        assertThat(balance).isEqualTo(2500L);
    }

    @Test
    void getAccountBalance_miss_returnsNull() throws Exception {
        when(searchService.search(any(Request.class))).thenReturn(new Result());

        Long balance = tool.getAccountBalance("99999");

        assertThat(balance).isNull();
    }
}
