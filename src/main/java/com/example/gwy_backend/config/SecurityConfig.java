package com.example.gwy_backend.config; // 确保包名与你的项目一致

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod; // 如果所有都permitAll，这个可能就不需要了
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 配置CORS - 让Spring Security使用下面的 corsConfigurationSource Bean
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. 禁用CSRF (对于无状态API通常是安全的，即使是公开API，禁用也无妨)
                .csrf(csrf -> csrf.disable())

                // 3. 配置请求授权 - 允许所有请求
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // <--- 核心改动：允许所有请求，无需认证
                )

                // 4. 配置Session管理为无状态 (推荐用于REST API)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. 禁用默认的登录和HTTP Basic认证，因为我们不使用它们
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许的源列表
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
                // 如果你需要从其他IP或域名访问（例如手机热点时的电脑IP），也需要添加
        ));
        // 允许的方法列表
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // 允许的请求头列表
        // 明确指定允许的头部，而不是 "*"
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With"));
        // 是否允许发送凭证 (如 cookies) - 对于完全公开的API，如果前端不发送凭证，可以设为 false
        configuration.setAllowCredentials(true); // 如果前端可能发送 withCredentials，保持 true
        // 预检请求的有效时间 (秒)
        configuration.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用此CORS配置
        source.registerCorsConfiguration("/**", configuration); // <--- 应用到所有路径
        return source;
    }

    // 由于不需要用户系统，PasswordEncoder, AuthenticationManager, UserDetailsService
    // 和 JwtAuthenticationFilter 相关的Bean都可以移除或注释掉。
    // 这里已经将它们移除了。
}