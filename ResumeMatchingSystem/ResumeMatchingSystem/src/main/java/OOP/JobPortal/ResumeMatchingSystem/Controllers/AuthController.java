package OOP.JobPortal.ResumeMatchingSystem.Controllers;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.LoginRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.RegisterEmployerRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.RegisterJobSeekerRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.response.AuthResponse;
import OOP.JobPortal.ResumeMatchingSystem.Services.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController – public endpoints for registration and login.
 * WEEK 3 – Menu-driven system: these endpoints are the "entry menu" of the API.
 * No authentication required — anyone can register or login.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    /** POST /api/auth/register/seeker – register a new job seeker */
    @PostMapping("/register/seeker")
    @Operation(summary = "Register as a Job Seeker")
    public ResponseEntity<AuthResponse> registerSeeker(
            @Valid @RequestBody RegisterJobSeekerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerJobSeeker(request));
    }

    /** POST /api/auth/register/employer – register a new employer */
    @PostMapping("/register/employer")
    @Operation(summary = "Register as an Employer")
    public ResponseEntity<AuthResponse> registerEmployer(
            @Valid @RequestBody RegisterEmployerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerEmployer(request));
    }

    /** POST /api/auth/login – login for any user type */
    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

