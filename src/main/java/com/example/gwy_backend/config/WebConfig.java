package com.example.gwy_backend.config; // 确保包名正确

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Web MVC 配置类，用于配置 CORS (跨域资源共享) 等。
 */
@Configuration
public class WebConfig {

    /**
     * 配置全局 CORS 规则。
     *
     * @return WebMvcConfigurer 实例
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 对所有接口路径生效
                        // 明确指定允许跨域请求的源地址 (你的前端应用地址)
                        // 注意：当 allowCredentials 为 true 时，不能使用 "*"
                        .allowedOrigins("http://localhost:5173")
                        // 允许的 HTTP 请求方法
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        // 允许所有的请求头
                        .allowedHeaders("*")
                        // 是否允许发送 Cookie 等凭据信息
                        .allowCredentials(true)
                        // 可选：预检请求 (OPTIONS) 的有效时间 (秒)
                        .maxAge(3600);
            }
        };
    }

    // 如果有其他 Web 相关配置，也可以在这个类中添加 @Bean
}