package OOP.JobPortal.ResumeMatchingSystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtUtil – JSON Web Token creation, parsing, and validation.
 *
 * JWT format: header.payload.signature
 *   header:    algorithm used (HS256)
 *   payload:   user's email, role, issued-at, expiry time
 *   signature: proves the token was issued by us (signed with our secret)
 *
 * WEEK 4 – Encapsulation:
 *   getSigningKey() is private — callers cannot access the raw key.
 *   Only the public methods (generateToken, validateToken) are exposed.
 */
@Component
public class JwtUtil {

    /** Secret key from application.yml — must be 256+ bits for HS256 */
    @Value("${jwt.secret}")
    private String secret;

    /** Token validity duration from application.yml (default 24 hours) */
    @Value("${jwt.expiration}")
    private long expirationMs;

    /**
     * Builds the HMAC-SHA256 signing key from the secret string.
     * Private: the key never leaves this class.
     *
     * @return the signing key
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for a successfully authenticated user.
     *
     * @param userDetails the authenticated user (provides username/email)
     * @param role        the user's role (stored as a custom claim)
     * @return            signed JWT token string
     */
    public String generateToken(UserDetails userDetails, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())   // email as subject
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the email (subject) from a JWT token.
     *
     * @param token the JWT token string
     * @return      the email address stored in the token
     */
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extracts the role from a JWT token.
     *
     * @param token the JWT token string
     * @return      the role stored in the token
     */
    public String extractRole(String token) {
        return (String) Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role");
    }

    /**
     * Validates that a token:
     *   1. Belongs to the correct user (email matches)
     *   2. Has not expired
     *   3. Has a valid signature (not tampered with)
     *
     * @param token       the JWT token to validate
     * @param userDetails the user to validate against
     * @return            true if the token is valid
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            Date expiry  = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            boolean emailMatches = email.equals(userDetails.getUsername());
            boolean notExpired   = expiry.after(new Date());

            return emailMatches && notExpired;

        } catch (JwtException | IllegalArgumentException e) {
            // Token is invalid, expired, or tampered with
            System.err.println("[JWT] Invalid token: " + e.getMessage());
            return false;
        }
    }
}
