/**
 * JwtAuthenticationFilter 类用于在Spring Security框架中进行JWT（JSON Web Token）认证。
 * 它继承自 OncePerRequestFilter，确保每个请求只被过滤一次。
 */
package com.daisyPig.filter;

import com.daisyPig.exception.JwtAuthenticationException;
import com.daisyPig.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter 过滤器用于处理 JWT 认证逻辑。
 * 该过滤器继承自 OncePerRequestFilter，确保每个请求只被处理一次。
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 用于处理 JWT 相关操作的工具类
    private final JwtUtil jwtUtil;
    // 用于加载用户详细信息的服务
    private final UserDetailsService userDetailsService;

    /**
     * 构造函数，初始化 JwtUtil 和 UserDetailsService。
     *
     * @param jwtUtil            用于处理 JWT 的工具类
     * @param userDetailsService 用于加载用户详细信息的服务
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 处理每个请求的核心逻辑。
     * 从请求头中提取 JWT 令牌，验证其有效性，并将用户信息设置到安全上下文中。
     *
     * @param request     HTTP 请求对象
     * @param response    HTTP 响应对象
     * @param filterChain 过滤器链
     * @throws ServletException 如果在处理请求时发生 Servlet 异常
     * @throws IOException      如果在处理请求时发生 I/O 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 从请求头中获取 Authorization 字段
            String authHeader = request.getHeader("Authorization");

            // 检查 Authorization 字段是否存在且以 "Bearer " 开头
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // 提取 JWT 令牌（去掉 "Bearer " 前缀）
                String token = authHeader.substring(7);

                // 验证 JWT 令牌的有效性
                if (jwtUtil.validateToken(token)) {
                    // 从 JWT 令牌中提取用户名
                    String username = jwtUtil.getUsernameFromToken(token);
                    // 根据用户名加载用户详细信息
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 创建一个认证令牌对象，包含用户详细信息和权限
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    // 将认证令牌设置到安全上下文中
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            // 将请求传递给下一个过滤器
            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException e) {
            // 处理 JWT 认证异常，设置响应状态码为 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // 设置响应内容类型为 JSON
            response.setContentType("application/json");
            // 设置响应字符编码为 UTF-8
            response.setCharacterEncoding("UTF-8");
            // 构造 JSON 格式的错误响应
            String jsonResponse = String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\"}", e.getMessage());
            // 将错误响应写入响应体
            response.getWriter().write(jsonResponse);
        }
    }
}