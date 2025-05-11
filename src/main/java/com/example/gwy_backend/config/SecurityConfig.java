package com.example.gwy_backend.config; // 和你的 WebConfig 在同一个包或合适的配置包

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration; // 引入这个
import org.springframework.web.cors.CorsConfigurationSource; // 引入这个
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // 引入这个

import java.util.Arrays; // 引入这个

@Configuration
@EnableWebSecurity // 开启Web安全功能
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 配置CORS (非常重要，让Spring Security使用你的CORS配置)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. 禁用CSRF (如果你的API是无状态的，或者前端没有处理CSRF token)
                //    对于主要由JS客户端调用的API，通常可以禁用
                .csrf(csrf -> csrf.disable())

                // 3. 配置请求授权规则
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/**").permitAll() // <<< 允许所有对 /api/** 的请求（暂时）
                        // 如果某些API需要认证，你可以配置更细致的规则，例如：
                        // .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // .requestMatchers("/api/user/**").authenticated()
                        .anyRequest().authenticated() // 其他所有请求都需要认证 (例如Spring Boot Actuator端点)
                )

                // 4. 配置Session管理 (对于REST API，通常是无状态的)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. 配置表单登录或HTTP Basic认证 (如果你的API不需要浏览器登录，可以简化或移除)
                //    由于我们上面允许了所有 /api/**，所以默认的登录页重定向可能不会发生
                //    但为了避免其他路径触发登录，可以这样配置：
                .formLogin(form -> form.disable()) // 禁用表单登录，避免重定向
                .httpBasic(httpBasic -> httpBasic.disable()); // 禁用HTTP Basic认证

        return http.build();
    }

    // 定义CORS配置源 (与WebConfig中的配置类似，但这是给Spring Security用的)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
                // 如果还有其他源，也加进来
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // 允许所有头部
        configuration.setAllowCredentials(true); // 如果需要凭证
        configuration.setMaxAge(3600L); // 预检请求的缓存时间

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // 对 /api/** 路径应用此CORS配置
        // 如果有其他路径也需要CORS，可以继续调用 source.registerCorsConfiguration()
        return source;
    }

    // 如果你使用了Spring Security的UserDetailsService，这里可以定义一个内存用户或从数据库加载
    // 例如，对于上面生成的密码，你可以这样配置一个内存用户（仅供开发测试）：
    /*
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("user")
            .password("{noop}d1fe191a-218e-4edb-b66f-10ba1252731a") // {noop} 表示密码未加密
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
    */
}