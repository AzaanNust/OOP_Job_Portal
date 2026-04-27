package OOP.JobPortal.ResumeMatchingSystem.Services.impl;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.LoginRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.RegisterEmployerRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.RegisterJobSeekerRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.response.AuthResponse;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Admin;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Employer;
import OOP.JobPortal.ResumeMatchingSystem.Entities.JobSeeker;
import OOP.JobPortal.ResumeMatchingSystem.Exceptions.BusinessException;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.AdminRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.EmployerRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.JobSeekerRepository;
import OOP.JobPortal.ResumeMatchingSystem.Security.JwtUtil;
import OOP.JobPortal.ResumeMatchingSystem.Services.AbstractAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ============================================================
 * AuthService  –  Registration and Login Business Logic
 * ============================================================
 *
 * WEEK 3 – Functionality (Input → Process → Output):
 *   Input:   registration/login data from the HTTP request
 *   Process: validate, hash password, save to DB, generate JWT
 *   Output:  AuthResponse containing the JWT token + user info
 *
 * WEEK 12 – Exception Handling:
 *   Throws BusinessException (custom unchecked exception) when
 *   a user tries to register with an already-used email address.
 *
 * WEEK 13 – Single Responsibility Principle (SRP):
 *   AuthService only handles authentication: register and login.
 *   It does not handle resume management, job search, or notifications.
 *
 * @Transactional: if anything fails during registration
 *   (e.g., a database constraint violation), the entire operation
 *   is rolled back — no partial saves.
 * ============================================================
 */
@Service
public class AuthService extends AbstractAuthService {

    // ── Dependencies injected by Spring ───────────────────────────

    @Autowired
    private JobSeekerRepository jobSeekerRepo;

    @Autowired
    private EmployerRepository employerRepo;

    @Autowired
    private AdminRepository adminRepo;

    /**
     * PasswordEncoder uses BCrypt to hash passwords.
     * BCrypt is one-way: you can verify a password, but never reverse the hash.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authManager;

    // ── Registration Methods ───────────────────────────────────────

    /**
     * Registers a new job seeker.
     *
     * STEPS:
     *   1. Check email uniqueness across all user tables
     *   2. Create a JobSeeker entity (using the inheritance constructor)
     *   3. Hash the password with BCrypt
     *   4. Save to the database
     *   5. Generate and return a JWT token (user is immediately logged in)
     *
     * @param req  registration request containing name, email, password, preferences
     * @return     AuthResponse with JWT token and user info
     * @throws BusinessException if the email is already registered
     */
    @Transactional
    public AuthResponse registerJobSeeker(RegisterJobSeekerRequest req) {

        // WEEK 12 – Exception Handling: check business rule before proceeding
        if (emailAlreadyExists(req.getEmail())) {
            throw new BusinessException(
                    "An account with email '" + req.getEmail() + "' already exists.");
        }

        // WEEK 5/6 – Inheritance: using JobSeeker's constructor that chains to User
        JobSeeker seeker = new JobSeeker(
                req.getFullName(),
                req.getEmail(),
                passwordEncoder.encode(req.getPassword())  // Hash password before storing
        );

        // Set optional seeker-specific fields using setters (Week 4)
        seeker.setPhoneNumber(req.getPhoneNumber());
        seeker.setPreferredLocation(req.getPreferredLocation());
        seeker.setPreferredShift(req.getPreferredShift());
        seeker.setTotalExperienceYears(req.getTotalExperienceYears());
        seeker.setProfileSummary(req.getProfileSummary());

        // WEEK 3 – File handling: save to DB (persistence = "file" in this context)
        jobSeekerRepo.save(seeker);

        // Generate JWT and return
        String token = jwtUtil.generateToken(seeker, seeker.getRole().name());
        return new AuthResponse(token, seeker.getId(), seeker.getFullName(),
                seeker.getEmail(), seeker.getRole());
    }

    /**
     * Registers a new employer.
     *
     * WEEK 4 – Method Overloading (conceptually):
     *   registerJobSeeker and registerEmployer share similar structure
     *   but handle different entity types — this is service-layer overloading.
     *
     * @param req  employer registration request
     * @return     AuthResponse with JWT token
     * @throws BusinessException if email is already registered
     */
    @Transactional
    public AuthResponse registerEmployer(RegisterEmployerRequest req) {

        if (emailAlreadyExists(req.getEmail())) {
            throw new BusinessException(
                    "An account with email '" + req.getEmail() + "' already exists.");
        }

        // WEEK 5/6 – Inheritance: Employer constructor chains to User
        Employer employer = new Employer(
                req.getFullName(),
                req.getEmail(),
                passwordEncoder.encode(req.getPassword()),
                req.getCompanyName()
        );

        employer.setPhoneNumber(req.getPhoneNumber());
        employer.setCompanyWebsite(req.getCompanyWebsite());
        employer.setIndustry(req.getIndustry());
        employer.setCompanyLocation(req.getCompanyLocation());
        employer.setCompanyDescription(req.getCompanyDescription());
        employer.setCompanySize(req.getCompanySize());

        employerRepo.save(employer);

        String token = jwtUtil.generateToken(employer, employer.getRole().name());
        return new AuthResponse(token, employer.getId(), employer.getFullName(),
                employer.getEmail(), employer.getRole());
    }

    /**
     * Logs in any type of user (seeker, employer, or admin).
     *
     * Spring Security's AuthenticationManager:
     *   - Calls CustomUserDetailsService.loadUserByUsername(email)
     *   - Checks the password against the stored BCrypt hash
     *   - Throws BadCredentialsException if wrong (handled by GlobalExceptionHandler)
     *
     * @param req  login request with email and password
     * @return     AuthResponse with fresh JWT token
     */
    public AuthResponse login(LoginRequest req) {

        // This line does all the authentication work:
        // 1. Looks up user by email
        // 2. Verifies password
        // 3. Throws BadCredentialsException if wrong
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        // The principal is the UserDetails object (our User entity)
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        // Extract role (remove "ROLE_" prefix Spring Security adds)
        String role = userDetails.getAuthorities()
                .iterator().next()
                .getAuthority()
                .replace("ROLE_", "");

        // Fetch the full entity to get id, fullName, etc.
        return switch (role) {
            case "JOB_SEEKER" -> {
                JobSeeker s = jobSeekerRepo.findByEmail(req.getEmail()).orElseThrow();
                yield new AuthResponse(jwtUtil.generateToken(userDetails, role),
                        s.getId(), s.getFullName(), s.getEmail(), s.getRole());
            }
            case "EMPLOYER" -> {
                Employer e = employerRepo.findByEmail(req.getEmail()).orElseThrow();
                yield new AuthResponse(jwtUtil.generateToken(userDetails, role),
                        e.getId(), e.getFullName(), e.getEmail(), e.getRole());
            }
            case "ADMIN" -> {
                Admin a = adminRepo.findByEmail(req.getEmail()).orElseThrow();
                yield new AuthResponse(jwtUtil.generateToken(userDetails, role),
                        a.getId(), a.getFullName(), a.getEmail(), a.getRole());
            }
            default -> throw new BusinessException("Unknown user role: " + role);
        };
    }

    // ── Private Helper Methods ─────────────────────────────────────

    /**
     * Checks if an email exists in any of the three user tables.
     * WEEK 4 – Encapsulation: private helper — callers don't need to know
     * which tables are checked, only the result.
     *
     * @param email the email to check
     * @return      true if already registered in any user table
     */
    private boolean emailAlreadyExists(String email) {
        return jobSeekerRepo.existsByEmail(email)
                || employerRepo.existsByEmail(email)
                || adminRepo.existsByEmail(email);
    }
}
