package com.search.mcp;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class McpInfoContributor implements InfoContributor {

    private final ApplicationContext applicationContext;
    private volatile List<Map<String, String>> cachedTools;

    public McpInfoContributor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public synchronized void contribute(Info.Builder builder) {
        if (cachedTools == null) {
            cachedTools = discoverTools();
        }
        builder.withDetail("mcp", Map.of("tools", cachedTools));
    }

    private List<Map<String, String>> discoverTools() {
        List<Map<String, String>> tools = new ArrayList<>();
        applicationContext.getBeansOfType(McpToolAdapter.class).values().forEach(adapter ->
                adapter.toolSpecs().forEach(spec -> {
                    Map<String, String> info = new LinkedHashMap<>();
                    info.put("name", spec.tool().name());
                    info.put("description", spec.tool().description());
                    info.put("version", "1");
                    tools.add(info);
                })
        );
        return tools;
    }
}
