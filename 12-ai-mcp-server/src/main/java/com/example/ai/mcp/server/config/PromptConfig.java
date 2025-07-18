package com.example.ai.mcp.server.config;

import com.example.ai.mcp.server.tools.PromptProvider;
import com.logaritex.mcp.spring.SpringAiMcpAnnotationProvider;
import io.modelcontextprotocol.server.McpServerFeatures;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author bootystar
 */
@Configuration(proxyBeanMethods = false)
public class PromptConfig {
    @Bean
    public List<McpServerFeatures.SyncPromptSpecification> promptSpecs(PromptProvider promptProvider) {
        return SpringAiMcpAnnotationProvider.createSyncPromptSpecifications(List.of(promptProvider));
    }
}
