package com.example.doc.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bootystar
 */
@Configuration
public class DocConfig {
    /*
    springfox和springdoc注解映射关系：
    @Api -> @Tag
    @ApiIgnore -> @Parameter(hidden = true) or @Operation(hidden = true) or @Hidden
    @ApiImplicitParam -> @Parameter
    @ApiImplicitParams -> @Parameters
    @ApiModel -> @Schema
    @ApiModelProperty(hidden = true) -> @Schema(accessMode = READ_ONLY)
    @ApiModelProperty -> @Schema
    @ApiOperation(value = "foo", notes = "bar") -> @Operation(summary = "foo", description = "bar")
    @ApiParam -> @Parameter
    @ApiResponse(code = 404, message = "foo") -> @ApiResponse(responseCode = "404", description = "foo")
     */


    /**
     * 分组设置
     * 此处的分组设置是为了在swagger-ui中显示不同的分组(右上角的下拉框)
     *
     * @return {@code GroupedOpenApi }
     * @author bootystar
     */
    @Bean
    public GroupedOpenApi empApi() {
        return GroupedOpenApi.builder()
                .group("员工管理") // 分组名称
                .pathsToMatch("/emp/**","/emp") // 匹配的请求路径
                .build();
    }

    /**
     * 部门api
     * 此处的分组设置是为了在swagger-ui中显示不同的分组(右上角的下拉框)
     *
     * @return {@code GroupedOpenApi }
     * @author bootystar
     */
    @Bean
    public GroupedOpenApi deptApi() {
        return GroupedOpenApi.builder()
                .group("部门管理") // 分组名称
                .pathsToMatch("/dept/**","/dept") // 匹配的请求路径
                .build();
    }

    /**
     * 文档信息
     * 此处设置为在左下角显示的信息
     *
     * @return {@code OpenAPI }
     * @author bootystar
     */
    @Bean
    public OpenAPI docsOpenAPI() {
        // 文档标题
        Info info = new Info()
                .title("文档标题")
                .description("文档描述")
                .version("v0.0.1")
                .license(new License().name("Apache 2.0").url("https://springdoc.org")) //开源许可证
        ;

        // 认证信息
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY) //认证类型
                .in(SecurityScheme.In.HEADER) // 认证位置
                .name("Authorization");// 认证名称
        ;


        // 组件(添加认证信息)
        Components components = new Components()
                .addSecuritySchemes("Authorization",securityScheme)
        ;


        // 指定请求需要携带的认证
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Authorization")
        ;

        // 服务器地址
        Server url = new Server().url("http://127.0.0.1");
        Server url1 = new Server().url("http://127.0.0.1:8080");

        //外部文档
        ExternalDocumentation documentation = new ExternalDocumentation()
                .description("外部文档描述") // 外部文档描述
                .url("https://springshop.wiki.github.org/docs") // 外部文档地址
                ;

        return new OpenAPI()
                .info(info) // 文档    .info(info) // 文档
                .components(components) // 组件
                .addSecurityItem(securityRequirement) // 认证信息
                .addServersItem(url) //请求前缀(服务器地址)
                .addServersItem(url1) //请求前缀(服务器地址)
                .externalDocs(documentation) // 外部文档(非必须)
        ;
    }
}
