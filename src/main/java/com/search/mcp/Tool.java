package com.search.mcp;

import java.lang.annotation.*;

/**
 * Reserved for future annotation processor; runtime uses {@link McpToolAdapter#toolSpecs()}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tool {
    String name();

    String description();

    String inputSchema() default "{}";

    int version() default 1;
}
