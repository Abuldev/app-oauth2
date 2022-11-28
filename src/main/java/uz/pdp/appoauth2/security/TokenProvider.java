package uz.pdp.appoauth2.security;

import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import uz.pdp.appoauth2.payload.UserPrincipal;

import java.util.Date;
import java.util.UUID;

@Component
public class TokenProvider {


    String tokeKey = "UxlamaB20";

    public String createToken(UserPrincipal userPrincipal, boolean forAccessToken) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 86_400_000);

        return Jwts
                .builder()
                .setSubject(userPrincipal.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, (
                        tokeKey
                ))
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokeKey)
                .parseClaimsJws(token)
                .getBody();

        return UUID.fromString(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts
                    .parser()
                    .setSigningKey(tokeKey)
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            System.err.println("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            System.err.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.err.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.err.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.err.println("JWT claims string is empty.");
        }
        return false;
    }
}