package projet.Configurations;

import projet.model.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.util.Base64;
import java.util.Date;

@Component
public class JWTManager {

    private static final String secret = "DvxMWzlQ2d6zSQ77EseNcGI1x0hhpCVJwtXBIx4c7uUlDSSRCD4kBXFyzEY2zLdN";
    private static final SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));

    public static String generateToken(Utilisateur utilisateur) {
        Date currentDate = new Date();
        return Jwts.builder()
                .setSubject(utilisateur.getEmail())
                .setHeaderParam("typ", "JWT")
                .setIssuedAt(currentDate)
                .setExpiration(new Date(currentDate.getTime() + dayToMs(1)))
                .claim("id", utilisateur.getId())
                .claim("email", utilisateur.getEmail())
                .claim("role", utilisateur.getRole().getNom())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Utilisateur utilisateur) {

        Date currentDate = new Date();

        return Jwts.builder()
                .setSubject(utilisateur.getEmail())
                .setIssuedAt(currentDate)
                .setExpiration(new Date(currentDate.getTime() + REFRESH_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException("Invalid token", ex.fillInStackTrace());
        }
    }

    public String getEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public void validateToken(String token) throws AuthenticationCredentialsNotFoundException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new AuthenticationCredentialsNotFoundException("Token expired", ex);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new AuthenticationCredentialsNotFoundException("Invalid token", ex.fillInStackTrace());
        }
    }

    private static final long REFRESH_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    private static final long dayToMs(int days) {
        return days * 24 * 60 * 60 * 1000L;
    }
}
