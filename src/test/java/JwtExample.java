import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;

public class JwtExample {
    // 生成安全的 HS256 密钥
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // JWT 过期时间（毫秒）
    private static final long EXPIRATION_TIME = 1800000;

    // 生成 JWT
    public static String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 验证 JWT
    public static String verifyToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            System.out.println("验证 JWT 时出错: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        // 模拟用户 ID
        String userId = "12345678";
        // 生成 JWT
        String token = generateToken(userId);
        System.out.println("生成的 JWT: " + token);

        // 验证 JWT
        String verifiedUserId = verifyToken(token);
        if (verifiedUserId != null) {
            System.out.println("验证成功，用户 ID: " + verifiedUserId);
        }
        System.out.println(token.length());
    }
}    