package OOP.JobPortal.ResumeMatchingSystem.Controllers;

import OOP.JobPortal.ResumeMatchingSystem.Entities.Employer;
import OOP.JobPortal.ResumeMatchingSystem.Entities.JobSeeker;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.ApplicationRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.EmployerRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.JobListingRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.JobSeekerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** AdminController – platform administration (Admin role only) */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Platform administration endpoints (Admin only)")
public class AdminController {

    @Autowired private JobSeekerRepository seekerRepo;
    @Autowired private EmployerRepository employerRepo;
    @Autowired private JobListingRepository jobRepo;
    @Autowired private ApplicationRepository appRepo;

    @GetMapping("/stats")
    @Operation(summary = "Get platform statistics")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(Map.of(
                "totalSeekers",      seekerRepo.count(),
                "totalEmployers",    employerRepo.count(),
                "totalJobs",         jobRepo.count(),
                "totalApplications", appRepo.count()
        ));
    }

    @GetMapping("/seekers")
    @Operation(summary = "Get all job seekers")
    public ResponseEntity<List<JobSeeker>> getAllSeekers() {
        return ResponseEntity.ok(seekerRepo.findAll());
    }

    @GetMapping("/employers")
    @Operation(summary = "Get all employers")
    public ResponseEntity<List<Employer>> getAllEmployers() {
        return ResponseEntity.ok(employerRepo.findAll());
    }

    @DeleteMapping("/seekers/{id}")
    @Operation(summary = "Deactivate a job seeker account (soft delete)")
    public ResponseEntity<Map<String, String>> deactivateSeeker(@PathVariable Long id) {
        seekerRepo.findById(id).ifPresent(s -> { s.setActive(false); seekerRepo.save(s); });
        return ResponseEntity.ok(Map.of("message", "Job seeker deactivated successfully"));
    }
}
