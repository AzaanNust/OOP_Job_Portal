package OOP.JobPortal.ResumeMatchingSystem.Config;

import OOP.JobPortal.ResumeMatchingSystem.Security.JwtAuthFilter;
import OOP.JobPortal.ResumeMatchingSystem.Security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * ============================================================
 * SecurityConfig  –  Spring Security Configuration
 * ============================================================
 *
 * This class is the single source of truth for ALL security
 * rules in the application. Every HTTP request passes through
 * the rules defined here before reaching any controller.
 *
 * WEEK 13 – SOLID Principles:
 *   Single Responsibility: this class only configures security.
 *   It does not handle business logic, database access, or routing.
 *
 * WEEK 8 – Interfaces:
 *   SecurityFilterChain, CorsConfigurationSource, PasswordEncoder,
 *   AuthenticationProvider, and AuthenticationManager are ALL interfaces.
 *   Spring Security defines the contract (interface); we provide the
 *   implementation via @Bean methods below.
 *
 * @Configuration  → tells Spring this class defines @Bean objects
 * @EnableWebSecurity → activates Spring Security for the application
 * @EnableMethodSecurity → enables @PreAuthorize on controller methods,
 *   allowing role-based access control at the method level
 *   e.g. @PreAuthorize("hasRole('EMPLOYER')") on a controller method
 * ============================================================
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * JwtAuthFilter intercepts every request and checks for a valid
     * JWT token in the Authorization header.
     * Injected via Dependency Injection (DI) — Week 13 DIP.
     */
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * CustomUserDetailsService loads user details from the database
     * by email during the login/authentication process.
     * This is the bridge between Spring Security and our User entities.
     */
    @Autowired
    private CustomUserDetailsService userDetailsService;

    // ================================================================
    // WEEK 8 – Interface Implementation via @Bean:
    //   Each method below returns an object that implements an interface.
    //   Spring stores these in the application context and injects them
    //   wherever the interface type is declared as a dependency.
    // ================================================================

    /**
     * SecurityFilterChain – defines the HTTP security rules.
     *
     * Every incoming HTTP request passes through this filter chain
     * in order, from top to bottom. The first matching rule wins.
     *
     * WEEK 12 – Exception Handling:
     *   If a request hits a protected endpoint without a valid JWT,
     *   Spring Security throws an AuthenticationException, which is
     *   caught and returns HTTP 403 Forbidden automatically.
     *
     * @param http  the HttpSecurity builder provided by Spring
     * @return      the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ── CSRF: disabled because we use JWT, not browser cookies ──
                // CSRF (Cross-Site Request Forgery) attacks only work when the
                // browser automatically sends session cookies with every request.
                // Our API uses JWT tokens in the Authorization header — the browser
                // does NOT send these automatically, so CSRF is not a risk here.
                .csrf(AbstractHttpConfigurer::disable)

                // ── CORS: restrict which origins can call this API ───────────
                // Delegates to corsConfigurationSource() bean defined below.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ── Sessions: STATELESS (no server-side session storage) ─────
                // Traditional web apps store session data on the server.
                // Our API is stateless — each request must carry its own JWT.
                // Spring Security will NEVER create an HttpSession for us.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── Route-level access control ───────────────────────────────
                // Rules are evaluated TOP TO BOTTOM — first match wins.
                .authorizeHttpRequests(auth -> auth

                        // Public: anyone can register or log in — no token needed
                        .requestMatchers("/api/auth/**").permitAll()

                        // Public: anyone can browse open jobs — no login required
                        // Only GET is permitted; POST/PUT/DELETE still require auth
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()

                        // Public: Swagger UI for development testing in a browser
                        // /swagger-ui.html  → the HTML page
                        // /swagger-ui/**    → JS, CSS, images Swagger UI loads
                        // /v3/api-docs/**   → OpenAPI JSON spec Swagger reads internally
                        // /webjars/**       → front-end libraries bundled with SpringDoc
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()

                        // Default: everything else requires a valid JWT token
                        // If no token → HTTP 401 Unauthorized
                        // If wrong role → HTTP 403 Forbidden
                        .anyRequest().authenticated()
                )

                // ── Wire our custom auth provider into the chain ─────────────
                .authenticationProvider(authenticationProvider())

                // ── Add our JWT filter BEFORE Spring's default username/password filter ──
                // This ensures JWT is checked first on every request.
                // If JWT is valid, Spring Security marks the user as authenticated
                // and skips the username/password filter entirely.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS Configuration – Android clients only.
     *
     * CORS (Cross-Origin Resource Sharing) is a browser security mechanism.
     * It controls which origins (websites) are allowed to make requests to this API.
     *
     * IMPORTANT: Android apps using Retrofit/OkHttp do NOT send an Origin header,
     * so CORS rules do NOT apply to them at all — Android calls always go through.
     * CORS only affects browser-based clients (like web apps or Swagger UI).
     *
     * Allowed origins (localhost only — for Swagger UI development testing):
     *   http://localhost:*   → Swagger UI opened in any local browser port
     *   http://127.0.0.1:*  → Alternative localhost address
     *   http://10.0.2.2:*   → Android emulator's address for the host machine
     *
     * Why NOT allowedOriginPatterns("*"):
     *   The old wildcard (*) allowed ANY website on the internet to call our API
     *   from a browser. Since we only have an Android frontend, this is unnecessary
     *   and a security risk — a malicious website could send requests as any user.
     *
     * @return configured CORS rules
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Only local origins — Swagger UI in browser + Android emulator
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",    // Swagger UI in any local browser
                "http://127.0.0.1:*",   // Alternative localhost
                "http://10.0.2.2:*"     // Android emulator → routes to host machine
        ));

        // Allow all standard HTTP methods used by our REST API
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allow all headers — including our custom Authorization: Bearer <token> header
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials (needed for Swagger UI's authentication flow)
        config.setAllowCredentials(true);

        // Apply these CORS rules to ALL routes in the application
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * BCrypt Password Encoder.
     *
     * BCrypt is a one-way hashing algorithm designed for passwords.
     * Properties:
     *   - One-way: you cannot reverse a BCrypt hash to get the password
     *   - Salted:  each hash includes a random salt, so identical passwords
     *              produce different hashes — prevents rainbow table attacks
     *   - Slow:    deliberately slow to compute, making brute-force infeasible
     *
     * WEEK 12 – Security:
     *   Passwords are NEVER stored as plain text. When a user registers,
     *   their password is hashed before being saved. When they log in,
     *   BCrypt verifies the plain-text attempt against the stored hash.
     *
     * WEEK 8 – Interface:
     *   PasswordEncoder is an interface. BCryptPasswordEncoder is the
     *   concrete implementation we choose to use.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication Provider.
     *
     * DaoAuthenticationProvider is Spring Security's standard implementation
     * for database-backed authentication. It:
     *   1. Calls userDetailsService.loadUserByUsername(email) to fetch the user
     *   2. Uses passwordEncoder.matches(rawPassword, storedHash) to verify password
     *   3. Returns an authenticated token if both checks pass
     *
     * WEEK 13 – Dependency Inversion Principle (DIP):
     *   AuthenticationProvider is an interface (abstraction).
     *   DaoAuthenticationProvider is the concrete implementation.
     *   AuthService depends on the AuthenticationManager abstraction,
     *   not on DaoAuthenticationProvider directly.
     *
     * @return configured DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Authentication Manager.
     *
     * AuthenticationManager is the main entry point for authentication in
     * Spring Security. AuthService calls it during login:
     *
     *   authManager.authenticate(
     *       new UsernamePasswordAuthenticationToken(email, password)
     *   );
     *
     * Internally, the manager delegates to our authenticationProvider()
     * which checks the database and verifies the BCrypt hash.
     * If credentials are wrong, BadCredentialsException is thrown.
     *
     * WEEK 12 – Exception Handling:
     *   BadCredentialsException is caught by GlobalExceptionHandler
     *   and returned as HTTP 401 Unauthorized with a clear error message.
     *
     * @param config  Spring's AuthenticationConfiguration (auto-provided)
     * @return        the configured AuthenticationManager
     * @throws Exception if manager cannot be built
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}