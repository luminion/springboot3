package com.example.ai.mcp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MCP服务器
 * <p>
 * 服务器分为2种类型, 可通过spring.ai.mcp.server.type=SYNC/ASYNC指定
 * SYNC同步服务器 - 这是使用 McpSyncServer 实现的默认服务器类型。它专为应用程序中的简单请求-响应模式而设计。
 * ASYNC异步服务器 - 异步服务器实现使用 McpAsyncServer ，并针对非阻塞操作进行了优化。
 * <p>
 * MCP 服务器支持四种主要能力类型，可以单独启用或禁用
 * 工具 - 使用 spring.ai.mcp.server.capabilities.tool=true|false 启用/禁用工具能力
 * 资源 - 使用 spring.ai.mcp.server.capabilities.resource=true|false 启用/禁用资源能力
 * 提示 - 使用 spring.ai.mcp.server.capabilities.prompt=true|false 启用/禁用提示能力
 * 补全 - 使用 spring.ai.mcp.server.capabilities.completion=true|false 启用/禁用补全功能
 * <p>
 * MCP 服务器支持三种传输机制，每种机制都有其专属的启动器
 * 标准输入/输出 (STDIO) - spring-ai-starter-mcp-server
 * Spring MVC (服务器发送事件) - spring-ai-starter-mcp-server-webmvc
 * Spring WebFlux (响应式 SSE) - spring-ai-starter-mcp-server-webflux
 *
 * @author bootystar
 */
@SpringBootApplication
public class AiMcpServerApp {

    public static void main(String[] args) {
        SpringApplication.run(AiMcpServerApp.class, args);
    }
    
    

    
}
