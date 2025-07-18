package com.example.ai.mcp.server.config;

import com.example.ai.mcp.server.tools.UserProfileResourceProvider;
import com.logaritex.mcp.spring.SpringAiMcpAnnotationProvider;
import io.modelcontextprotocol.server.McpServerFeatures;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author bootystar
 */
@Configuration(proxyBeanMethods = false)
public class ResourceConfig {

    @Bean
    public List<McpServerFeatures.SyncResourceSpecification> resourceSpecs(UserProfileResourceProvider userProfileResourceProvider) {
        return SpringAiMcpAnnotationProvider.createSyncResourceSpecifications(List.of(userProfileResourceProvider));
    }
}
