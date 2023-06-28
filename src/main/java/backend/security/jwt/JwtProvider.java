package backend.security.jwt;

import backend.security.userprincipal.UserPrinciple;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    private final String jwtSecret = "KeySecret";

    public String creatToken(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        long jwtExpiration = 86400;
        return Jwts.builder().setSubject(userPrinciple.getUsername())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(new Date().getTime() + jwtExpiration * 1000))
                   .signWith(SignatureAlgorithm.HS512, jwtSecret)
                   .compact();
    }

    public boolean validateToken(String token) {
        /*ExpiredJwtException,
        UnsupportedJwtException,
        MalformedJwtException,
        SignatureException,
        IllegalArgumentException;*/
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT Token -> {1}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT Token -> {1}", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid Format JWT Token -> {1}", e);
        } catch (IllegalArgumentException e) {
            logger.error("Illegal Argument JWT -> {1}", e);
        } catch (SignatureException e) {
            logger.error("Invalid JWT Signature -> {1}", e);
        }
        return false;
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
}
