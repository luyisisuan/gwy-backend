package com.example.gwy_backend.config;

import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // 如果不再需要，可以移除

@Configuration
public class WebConfig /* implements WebMvcConfigurer */ { // 如果没有其他配置，可以不实现接口

    // 如果WebConfig还有其他配置Web MVC的Bean或方法，保留它们
    // 如果WebConfig唯一的职责是CORS，并且现在由SecurityConfig处理，
    // 那么这个WebConfig类中关于CORS的部分可以被注释掉或删除。

    /*
    // 如果选择让SecurityConfig全权负责CORS，可以注释或删除以下方法
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 内容可以为空，或者完全移除这个方法和 WebMvcConfigurer 的实现
        // 因为SecurityConfig中的 .cors() 配置会优先
    }
    */
}