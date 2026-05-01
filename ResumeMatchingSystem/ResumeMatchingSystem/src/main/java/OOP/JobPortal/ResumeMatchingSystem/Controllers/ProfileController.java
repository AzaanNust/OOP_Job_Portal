package OOP.JobPortal.ResumeMatchingSystem.Controllers;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.UpdateEmployerRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.UpdateSeekerRequest;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Employer;
import OOP.JobPortal.ResumeMatchingSystem.Entities.JobSeeker;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.EmployerRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.JobSeekerRepository;
import OOP.JobPortal.ResumeMatchingSystem.Exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ProfileController – GET and UPDATE profile endpoints.
 * Android-only: no web views, no HTML, pure JSON responses.
 *
 * Endpoints:
 *   GET  /api/profile/seeker       → get seeker's own profile
 *   PUT  /api/profile/seeker       → update seeker's profile
 *   GET  /api/profile/employer     → get employer's own profile
 *   PUT  /api/profile/employer     → update employer's profile + company
 */
@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "View and update user profiles")
public class ProfileController {

    @Autowired
    private JobSeekerRepository seekerRepo;

    @Autowired
    private EmployerRepository employerRepo;

    // ── Job Seeker ─────────────────────────────────────────────────

    /** GET /api/profile/seeker — returns logged-in seeker's profile */
    @GetMapping("/seeker")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Get the logged-in job seeker's profile")
    public ResponseEntity<Map<String, Object>> getSeekerProfile(
            @AuthenticationPrincipal JobSeeker seeker) {
        return ResponseEntity.ok(seekerToMap(seeker));
    }

    /** PUT /api/profile/seeker — update seeker's profile fields */
    @PutMapping("/seeker")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Update job seeker profile")
    public ResponseEntity<Map<String, Object>> updateSeekerProfile(
            @AuthenticationPrincipal JobSeeker seeker,
            @RequestBody UpdateSeekerRequest req) {

        JobSeeker managed = seekerRepo.findById(seeker.getId())
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker", seeker.getId()));

        if (req.getFullName()    != null && !req.getFullName().isBlank())
            managed.setFullName(req.getFullName().trim());
        if (req.getPhoneNumber() != null)
            managed.setPhoneNumber(req.getPhoneNumber().trim());
        if (req.getPreferredLocation() != null)
            managed.setPreferredLocation(req.getPreferredLocation().trim());
        if (req.getPreferredShift() != null)
            managed.setPreferredShift(req.getPreferredShift());
        if (req.getTotalExperienceYears() >= 0)
            managed.setTotalExperienceYears(req.getTotalExperienceYears());
        if (req.getProfileSummary() != null)
            managed.setProfileSummary(req.getProfileSummary().trim());

        seekerRepo.save(managed);
        return ResponseEntity.ok(seekerToMap(managed));
    }

    // ── Employer ───────────────────────────────────────────────────

    /** GET /api/profile/employer — returns logged-in employer's profile */
    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get the logged-in employer's profile and company info")
    public ResponseEntity<Map<String, Object>> getEmployerProfile(
            @AuthenticationPrincipal Employer employer) {
        return ResponseEntity.ok(employerToMap(employer));
    }

    /** PUT /api/profile/employer — update employer's profile and company */
    @PutMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Update employer profile and company details")
    public ResponseEntity<Map<String, Object>> updateEmployerProfile(
            @AuthenticationPrincipal Employer employer,
            @RequestBody UpdateEmployerRequest req) {

        Employer managed = employerRepo.findById(employer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer", employer.getId()));

        if (req.getFullName()            != null && !req.getFullName().isBlank())
            managed.setFullName(req.getFullName().trim());
        if (req.getPhoneNumber()         != null)
            managed.setPhoneNumber(req.getPhoneNumber().trim());
        if (req.getCompanyName()         != null && !req.getCompanyName().isBlank())
            managed.setCompanyName(req.getCompanyName().trim());
        if (req.getIndustry()            != null)
            managed.setIndustry(req.getIndustry().trim());
        if (req.getCompanyLocation()     != null)
            managed.setCompanyLocation(req.getCompanyLocation().trim());
        if (req.getCompanyWebsite()      != null)
            managed.setCompanyWebsite(req.getCompanyWebsite().trim());
        if (req.getCompanyDescription()  != null)
            managed.setCompanyDescription(req.getCompanyDescription().trim());
        if (req.getCompanySize()         != null)
            managed.setCompanySize(req.getCompanySize().trim());

        employerRepo.save(managed);
        return ResponseEntity.ok(employerToMap(managed));
    }

    // ── Helpers ────────────────────────────────────────────────────

    private Map<String, Object> seekerToMap(JobSeeker s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",                   s.getId());
        m.put("fullName",             s.getFullName());
        m.put("email",                s.getEmail());
        m.put("phoneNumber",          s.getPhoneNumber());
        m.put("preferredLocation",    s.getPreferredLocation());
        m.put("preferredShift",       s.getPreferredShift());
        m.put("totalExperienceYears", s.getTotalExperienceYears());
        m.put("profileSummary",       s.getProfileSummary());
        m.put("role",                 s.getRole());
        return m;
    }

    private Map<String, Object> employerToMap(Employer e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",                 e.getId());
        m.put("fullName",           e.getFullName());
        m.put("email",              e.getEmail());
        m.put("phoneNumber",        e.getPhoneNumber());
        m.put("companyName",        e.getCompanyName());
        m.put("industry",           e.getIndustry());
        m.put("companyLocation",    e.getCompanyLocation());
        m.put("companyWebsite",     e.getCompanyWebsite());
        m.put("companyDescription", e.getCompanyDescription());
        m.put("companySize",        e.getCompanySize());
        m.put("role",               e.getRole());
        return m;
    }
}