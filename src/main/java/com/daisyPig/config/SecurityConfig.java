/**
 * 配置类，用于配置Spring Security的安全策略
 */
package com.daisyPig.config;

import com.daisyPig.filter.JwtAuthenticationFilter;
import com.daisyPig.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 配置Spring Security的安全策略
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Jwt工具类
    private final JwtUtil jwtUtil;
    // 用户详情服务
    private final UserDetailsService userDetailsService;

    /**
     * 构造函数，注入JwtUtil和UserDetailsService
     *
     * @param jwtUtil            Jwt工具类
     * @param userDetailsService 用户详情服务
     */
    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 配置安全过滤器链
     *
     * @param http HttpSecurity对象
     * @return 安全过滤器链
     * @throws Exception 配置过程中可能出现的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护
            .csrf(csrf -> csrf.disable())
            // 配置会话创建策略为无状态
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置请求授权规则
            .authorizeHttpRequests(auth -> auth
                // 允许访问注册和登录接口
                .requestMatchers("/api/register", "/api/login").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            // 在UsernamePasswordAuthenticationFilter之前添加JwtAuthenticationFilter
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}