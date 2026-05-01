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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * JobController – job listing endpoints.
 * GET endpoints are public. POST/PUT/PATCH require EMPLOYER role.
 */
@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Jobs", description = "Browse, post, and manage job listings")
public class JobController {

    @Autowired
    private JobService jobService;

    /**
     * GET /api/jobs?title=Java&location=Lahore&shift=NIGHT
     * Returns a plain List — avoids PageImpl serialization issues with Android/Gson.
     */
    @GetMapping
    @Operation(summary = "Search jobs by title, location and shift type")
    public ResponseEntity<List<JobListingResponse>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) ShiftType shift) {
        return ResponseEntity.ok(jobService.searchJobs(title, location, shift));
    }

    /** GET /api/jobs/{id} */
    @GetMapping("/{id}")
    @Operation(summary = "Get job details by ID")
    public ResponseEntity<JobListingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    /** GET /api/jobs/my-jobs – employer views their own jobs */
    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get all jobs posted by the logged-in employer")
    public ResponseEntity<List<JobListingResponse>> getMyJobs(
            @AuthenticationPrincipal Employer employer) {
        return ResponseEntity.ok(jobService.getJobsByEmployer(employer.getId()));
    }

    /** POST /api/jobs – employer posts a new job */
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Post a new job listing (Employer only)")
    public ResponseEntity<JobListingResponse> postJob(
            @AuthenticationPrincipal Employer employer,
            @Valid @RequestBody JobListingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.postJob(employer.getId(), request));
    }

    /** PUT /api/jobs/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Update a job listing (Employer only)")
    public ResponseEntity<JobListingResponse> updateJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer,
            @Valid @RequestBody JobListingRequest request) {
        return ResponseEntity.ok(jobService.updateJob(id, employer.getId(), request));
    }

    /** PATCH /api/jobs/{id}/close */
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Close a job listing (Employer only)")
    public ResponseEntity<JobListingResponse> closeJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer) {
        return ResponseEntity.ok(jobService.closeJob(id, employer.getId()));
    }

    /** PATCH /api/jobs/{id}/reactivate */
    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Reactivate a closed job listing (Employer only)")
    public ResponseEntity<JobListingResponse> reactivateJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer) {
        return ResponseEntity.ok(jobService.reactivateJob(id, employer.getId()));
    }

    /** DELETE /api/jobs/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Delete a job listing permanently (Employer only)")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer) {
        jobService.deleteJob(id, employer.getId());
        return ResponseEntity.noContent().build();
    }
}