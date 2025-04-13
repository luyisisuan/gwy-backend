package com.example.gwy_backend.config; // 替换为你的包名

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 匹配 /api 下的所有路径
                .allowedOrigins("http://localhost:5173") // 允许来自 Vue 开发服务器的请求
                // 如果你部署了 Vue 应用，也需要添加部署后的域名
                // .allowedOrigins("http://localhost:5173", "https://your-vue-app.com")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 允许的方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 是否允许发送 Cookie
                .maxAge(3600); // 预检请求（OPTIONS）的缓存时间（秒）
    }
}