package OOP.JobPortal.ResumeMatchingSystem.Security;

/*
 * ================================
 * SPRING SECURITY JWT FILTER FILE
 * ================================
 * This class is responsible for:
 * → Intercepting every HTTP request
 * → Extracting JWT token from request header
 * → Validating the token
 * → Setting authentication in Spring Security context
 */

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
 * @Component
 * → Marks this class as a Spring-managed bean
 * → Spring automatically detects and registers it
 *
 * OncePerRequestFilter
 * → Ensures this filter runs ONLY ONCE per HTTP request
 * → Prevents duplicate authentication processing
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    /*
     * =========================
     * DEPENDENCY INJECTION
     * =========================
     * @Autowired tells Spring to automatically inject these beans
     */

    @Autowired
    private JwtUtil jwtUtil;
    // JwtUtil:
    // → Responsible for JWT operations:
    //   - generate token
    //   - extract claims (email, role)
    //   - validate token signature & expiry

    @Autowired
    private CustomUserDetailsService userDetailsService;
    // CustomUserDetailsService:
    // → Loads user from database
    // → Implements Spring Security UserDetailsService

    /**
     * MAIN FILTER METHOD
     * This method runs for EVERY HTTP REQUEST
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,       // Incoming HTTP request
            HttpServletResponse response,     // Outgoing HTTP response
            FilterChain filterChain           // Chain of filters
    ) throws ServletException, IOException {

        /*
         * STEP 1: READ AUTHORIZATION HEADER
         * Example:
         * Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
         */
        String authHeader = request.getHeader("Authorization");

        /*
         * STEP 2: CHECK IF HEADER EXISTS AND IS VALID FORMAT
         * If no token → skip authentication and continue request
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * STEP 3: EXTRACT RAW JWT TOKEN
         * "Bearer " is 7 characters → removed using substring(7)
         */
        String token = authHeader.substring(7);

        try {

            /*
             * STEP 4: EXTRACT EMAIL (SUBJECT) FROM TOKEN
             * JWT "sub" field = email in this system design
             */
            String email = jwtUtil.extractEmail(token);

            /*
             * STEP 5: CHECK IF USER IS NOT ALREADY AUTHENTICATED
             * SecurityContextHolder holds authentication for current request
             */
            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                /*
                 * STEP 6: LOAD USER FROM DATABASE
                 * Converts email → full UserDetails object
                 */
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                /*
                 * STEP 7: VALIDATE TOKEN
                 * Checks:
                 * → signature is valid (not tampered)
                 * → token is not expired
                 * → token belongs to correct user
                 */
                if (jwtUtil.validateToken(token, userDetails)) {

                    /*
                     * STEP 8: CREATE AUTHENTICATION OBJECT
                     *
                     * UsernamePasswordAuthenticationToken contains:
                     * → Principal (user)
                     * → Credentials (null because JWT used instead of password)
                     * → Authorities (roles/permissions)
                     */
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /*
                     * STEP 9: ADD REQUEST DETAILS
                     * Adds metadata like:
                     * → IP address
                     * → session ID
                     */
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    /*
                     * STEP 10: SET AUTHENTICATION IN SECURITY CONTEXT
                     * THIS IS THE MOST IMPORTANT STEP
                     *
                     * After this:
                     * → User is considered LOGGED IN
                     * → Controllers can access user info
                     * → @PreAuthorize works
                     */
                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            /*
             * If token is:
             * → invalid
             * → expired
             * → tampered
             *
             * We do NOT crash the app
             * We simply log and treat request as unauthenticated
             */
            System.err.println("[JwtAuthFilter] Token validation failed: "
                    + e.getMessage());
        }

        /*
         * STEP 11: CONTINUE FILTER CHAIN
         * Pass request to next filter or controller
         * WITHOUT this → request will stop here
         */
        filterChain.doFilter(request, response);
    }
}