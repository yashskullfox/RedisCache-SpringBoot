package com.search.mcp;

import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public interface McpToolAdapter {
    List<McpServerFeatures.SyncToolSpecification> toolSpecs();
}
