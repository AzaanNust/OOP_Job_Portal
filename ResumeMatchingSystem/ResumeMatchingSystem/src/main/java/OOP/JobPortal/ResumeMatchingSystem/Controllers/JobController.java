package OOP.JobPortal.ResumeMatchingSystem.Controllers;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.JobListingRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.response.JobListingResponse;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Employer;
import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;
import OOP.JobPortal.ResumeMatchingSystem.Services.impl.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * JobController – job listing endpoints.
 *
 * ROUTE ORDER IS CRITICAL: specific paths (/my-jobs) MUST come
 * before wildcard paths (/{id}), otherwise Spring tries to parse
 * "my-jobs" as a Long and throws NumberFormatException.
 */
@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Jobs", description = "Browse, post, update, delete job listings")
public class JobController {

    @Autowired
    private JobService jobService;

    /** GET /api/jobs — search open jobs with optional filters */
    @GetMapping
    @Operation(summary = "Search jobs by title, location, and shift type")
    public ResponseEntity<Page<JobListingResponse>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) ShiftType shift,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.searchJobs(title, location, shift, page, size));
    }

    /**
     * GET /api/jobs/my-jobs — employer's own listings.
     * MUST be declared before /{id} — Spring matches top to bottom.
     */
    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get all jobs posted by the logged-in employer")
    public ResponseEntity<List<JobListingResponse>> getMyJobs(
            @AuthenticationPrincipal Employer employer) {
        return ResponseEntity.ok(jobService.getJobsByEmployer(employer.getId()));
    }

    /** GET /api/jobs/{id} — job detail (MUST come after /my-jobs) */
    @GetMapping("/{id}")
    @Operation(summary = "Get job details by ID")
    public ResponseEntity<JobListingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    /** POST /api/jobs — employer posts a new job */
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Post a new job listing")
    public ResponseEntity<JobListingResponse> postJob(
            @AuthenticationPrincipal Employer employer,
            @Valid @RequestBody JobListingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.postJob(employer.getId(), request));
    }

    /** PUT /api/jobs/{id} — employer updates an existing job */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Update a job listing (owner only)")
    public ResponseEntity<JobListingResponse> updateJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer,
            @Valid @RequestBody JobListingRequest request) {
        return ResponseEntity.ok(jobService.updateJob(id, employer.getId(), request));
    }

    /** PATCH /api/jobs/{id}/close — employer closes a job */
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Close a job listing (owner only)")
    public ResponseEntity<JobListingResponse> closeJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer) {
        return ResponseEntity.ok(jobService.closeJob(id, employer.getId()));
    }

    /** DELETE /api/jobs/{id} — employer permanently deletes a job */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Delete a job listing permanently (owner only)")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer) {
        jobService.deleteJob(id, employer.getId());
        return ResponseEntity.noContent().build();
    }
}