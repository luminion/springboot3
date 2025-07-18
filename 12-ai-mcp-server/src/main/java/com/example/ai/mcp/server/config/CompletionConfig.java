package com.example.ai.mcp.server.config;

import com.example.ai.mcp.server.tools.AutocompleteProvider;
import com.logaritex.mcp.spring.SpringAiMcpAnnotationProvider;
import io.modelcontextprotocol.server.McpServerFeatures;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author bootystar
 */
@Configuration(proxyBeanMethods = false)
public class CompletionConfig {
    @Bean
    public List<McpServerFeatures.SyncCompletionSpecification> completionSpecs(AutocompleteProvider autocompleteProvider) {
        return SpringAiMcpAnnotationProvider.createSyncCompleteSpecifications(List.of(autocompleteProvider));
    }
}
