package com.example.gwy_backend.config; // 确保包名与你的项目一致

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // <<< 引入 HttpMethod
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

                // 2. 禁用CSRF (对于无状态API通常是安全的)
                .csrf(csrf -> csrf.disable())

                // 3. 配置请求授权
                .authorizeHttpRequests(authz -> authz
                        // --- 新增/修改的规则，放在通用规则之前 ---
                        .requestMatchers(HttpMethod.POST, "/api/files/upload").permitAll() // 允许文件上传
                        .requestMatchers(HttpMethod.GET, "/api/files/download/**").permitAll() // 允许文件下载
                        // --- 原有的通用规则 ---
                        .requestMatchers("/api/**").permitAll() // 开发阶段：允许所有对/api/路径的请求
                        // 在生产环境中，你需要更细致地配置哪些API需要认证/授权
                        .anyRequest().authenticated() // 其他所有请求（如果存在）都需要认证
                )

                // 4. 配置Session管理为无状态 (推荐用于REST API)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. 禁用默认的登录和HTTP Basic认证，以避免重定向
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
                // 如果你需要从其他IP或域名访问（例如手机热点时的电脑IP，或生产环境的前端域名），也需要添加
        ));
        // 允许的方法列表
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // 允许的请求头列表
        configuration.setAllowedHeaders(List.of("*")); // 允许所有头部
        // 是否允许发送凭证 (如 cookies)
        configuration.setAllowCredentials(true);
        // 预检请求的有效时间 (秒)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有以 /api/ 开头的路径应用此CORS配置 (这也包括了 /api/files/**)
        source.registerCorsConfiguration("/api/**", configuration);
        // 如果有其他路径需要不同的CORS策略，可以继续注册
        return source;
    }

    // 如果需要，可以配置UserDetailsService等其他安全相关的Bean
    /*
    @Bean
    public UserDetailsService userDetailsService() {
        // 使用 PasswordEncoder (推荐)
        // PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // UserDetails user = User.builder()
        //     .username("user")
        //     .password(encoder.encode("your-generated-password-here")) // 替换为实际密码
        //     .roles("USER")
        //     .build();

        // 或者，仅供最简单的测试 (不推荐)
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("d1fe191a-218e-4edb-b66f-10ba1252731a") // 使用你日志中生成的密码
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
    */
}