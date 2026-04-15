package OOP.JobPortal.ResumeMatchingSystem.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter – Intercepts every HTTP request to check for a JWT token.
 *
 * Flow for each request:
 *   1. Read Authorization header → extract "Bearer <token>"
 *   2. Parse token → get email
 *   3. Load user from database
 *   4. Validate token
 *   5. If valid → set user in Spring Security context
 *   6. Pass request to the next filter/controller
 *
 * OncePerRequestFilter: guarantees this filter runs exactly once per request.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest  request,
                                    HttpServletResponse response,
                                    FilterChain         filterChain)
            throws ServletException, IOException {

        // Read the Authorization header from the HTTP request
        String authHeader = request.getHeader("Authorization");

        // If there is no token (e.g., public endpoint), skip authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Strip the "Bearer " prefix to get the raw token
        String token = authHeader.substring(7);

        try {
            String email = jwtUtil.extractEmail(token);

            // Only authenticate if the security context is empty (not yet authenticated)
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user details from the database
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Validate the token
                if (jwtUtil.validateToken(token, userDetails)) {
                    // Create an authentication object and set it in the security context
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Invalid token — don't authenticate, but don't crash the app
            System.err.println("[JwtAuthFilter] Token validation failed: " + e.getMessage());
        }

        // Continue with the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
