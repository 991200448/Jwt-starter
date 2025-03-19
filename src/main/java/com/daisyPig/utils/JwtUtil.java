// 定义该类所在的包
package com.daisyPig.utils;

// 导入自定义的 JWT 认证异常类
import com.daisyPig.exception.JwtAuthenticationException;
// 导入 JWT 相关的类
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
// 导入 Spring 框架的组件注解
import org.springframework.stereotype.Component;

// 导入加密相关的类
import javax.crypto.SecretKey;
// 导入日期类
import java.util.Date;
// 导入集合类
import java.util.HashSet;
import java.util.Set;

/**
 * JwtUtil 类用于处理 JWT（JSON Web Token）的生成、验证、失效和解析操作。
 * 该类使用 Spring 的 @Component 注解，以便可以被 Spring 框架自动扫描和管理。
 */
@Component
public class JwtUtil {
    // 用于签名和验证 JWT 的密钥
    private final SecretKey secret;
    // JWT 的过期时间，默认为 24 小时（以毫秒为单位）
    private final long expiration = 86400000;
    // 存储已失效的 JWT 令牌的黑名单
    private final Set<String> blacklist = new HashSet<>();

    /**
     * 构造函数，初始化用于签名和验证 JWT 的密钥。
     * 使用 Keys 工具类生成一个安全的 HS512 算法的密钥。
     */
    public JwtUtil() {
        // 使用Keys工具类生成安全的密钥
        this.secret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * 根据用户名生成 JWT 令牌。
     *
     * @param username 用户的用户名
     * @return 生成的 JWT 令牌
     */
    public String generateToken(String username) {
        return Jwts.builder()
                // 设置 JWT 的主题为用户名
                .setSubject(username)
                // 设置 JWT 的签发时间为当前时间
                .setIssuedAt(new Date())
                // 设置 JWT 的过期时间为当前时间加上预设的过期时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                // 使用 HS512 算法和密钥对 JWT 进行签名
                .signWith(SignatureAlgorithm.HS512, secret)
                // 构建并返回 JWT 字符串
                .compact();
    }

    /**
     * 验证 JWT 令牌的有效性。
     *
     * @param token 要验证的 JWT 令牌
     * @return 如果令牌有效，则返回 true；否则抛出 JwtAuthenticationException 异常
     * @throws JwtAuthenticationException 如果令牌为空、已失效或无效
     */
    public boolean validateToken(String token) {
        // 检查令牌是否为空
        if (token == null || token.isEmpty()) {
            throw new JwtAuthenticationException("Token cannot be empty");
        }
        // 检查令牌是否在黑名单中
        if (blacklist.contains(token)) {
            throw new JwtAuthenticationException("Token has been invalidated");
        }
        try {
            // 使用密钥解析 JWT 令牌
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            // 如果解析成功，则令牌有效
            return true;
        } catch (Exception e) {
            // 如果解析失败，则抛出异常表示令牌无效或已过期
            throw new JwtAuthenticationException("Invalid or expired token");
        }
    }

    /**
     * 将指定的 JWT 令牌标记为失效。
     *
     * @param token 要失效的 JWT 令牌
     * @throws JwtAuthenticationException 如果令牌为空或已经无效
     */
    public void invalidateToken(String token) {
        // 检查令牌是否为空
        if (token == null || token.isEmpty()) {
            throw new JwtAuthenticationException("Token cannot be empty");
        }
        // 检查令牌是否有效
        if (!validateToken(token)) {
            throw new JwtAuthenticationException("Cannot invalidate an invalid token");
        }
        // 将有效令牌添加到黑名单中
        blacklist.add(token);
    }

    /**
     * 从 JWT 令牌中提取用户名。
     *
     * @param token 要解析的 JWT 令牌
     * @return 提取的用户名
     */
    public String getUsernameFromToken(String token) {
        // 解析 JWT 令牌并获取其声明
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        // 返回声明中的主题，即用户名
        return claims.getSubject();
    }
}