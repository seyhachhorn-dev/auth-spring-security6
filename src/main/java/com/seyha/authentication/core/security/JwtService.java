package com.seyha.authentication.core.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

@Service
public class JwtService {
    // 1. Secret Key Management
    // Ideally, load this from environment variables.
    // For demo, this is a generated 256-bit key (Hex encoded).
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.expiration}")
    private long jwtExpiration;

    // 2. Extract Username (Subject)
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    // 3. Extract Specific Claim
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    // 4. Generate Token (Simple)
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    // 5. Generate Token (With Extra Claims like roles/permissions)
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // 1 Day
                .signWith(getSignInKey(), Jwts.SIG.HS256) // JJWT 0.12+ Syntax
                .compact();
    }


//    6. Validation

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
