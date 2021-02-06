package com.maxy.caller.admin.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

/**
 * @author maxy
 */
@Configuration
public class SwaggerConfig {

    @Value("${swagger.enable:true}")
    private boolean isEnable;

    @Bean
    public UiConfiguration uiConfiguration() {
        return UiConfigurationBuilder.builder().defaultModelsExpandDepth(-1).build();
    }

    @Bean
    public Docket eventTriggerDocket() {
        return new Docket(DocumentationType.SWAGGER_2).enable(isEnable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.maxy.caller.admin.controller"))
                .build()
                .apiInfo(apiInfo());
    }

    /**
     * 接口文档详细信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Caller API")
                .version("1.0")
                .build();
    }
}
