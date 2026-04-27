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

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Jobs", description = "Browse, post, update, delete, and reactivate job listings")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    @Operation(summary = "Search open jobs — all returned when no filter. Partial title match supported.")
    public ResponseEntity<Page<JobListingResponse>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) ShiftType shift,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(jobService.searchJobs(title, location, shift, page, size));
    }

    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Employer: get all my posted jobs")
    public ResponseEntity<List<JobListingResponse>> getMyJobs(
            @AuthenticationPrincipal Employer employer) {
        return ResponseEntity.ok(jobService.getJobsByEmployer(employer.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job detail by ID")
    public ResponseEntity<JobListingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Post a new job listing")
    public ResponseEntity<JobListingResponse> postJob(
            @AuthenticationPrincipal Employer employer,
            @Valid @RequestBody JobListingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.postJob(employer.getId(), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Update a job listing")
    public ResponseEntity<JobListingResponse> updateJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer,
            @Valid @RequestBody JobListingRequest request) {
        return ResponseEntity.ok(jobService.updateJob(id, employer.getId(), request));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Close a job listing")
    public ResponseEntity<JobListingResponse> closeJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer) {
        return ResponseEntity.ok(jobService.closeJob(id, employer.getId()));
    }

    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Reactivate a closed job back to OPEN")
    public ResponseEntity<JobListingResponse> reactivateJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer) {
        return ResponseEntity.ok(jobService.reactivateJob(id, employer.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Delete a job listing permanently")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer) {
        jobService.deleteJob(id, employer.getId());
        return ResponseEntity.noContent().build();
    }
}