package OOP.JobPortal.ResumeMatchingSystem.Controllers;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.ApplicationRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.response.ApplicationResponse;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Employer;
import OOP.JobPortal.ResumeMatchingSystem.Entities.JobSeeker;
import OOP.JobPortal.ResumeMatchingSystem.Services.impl.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ApplicationController – job application pipeline endpoints.
 * WEEK 3 – Menu-driven: seeker applies, employer manages the pipeline.
 */
@RestController
@RequestMapping("/api/applications")
@Tag(name = "Applications", description = "Apply for jobs and manage the hiring pipeline")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    /** POST /api/applications */
    @PostMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Apply for a job")
    public ResponseEntity<ApplicationResponse> apply(
            @AuthenticationPrincipal JobSeeker seeker,
            @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.apply(seeker.getId(), request));
    }

    /** GET /api/applications/my */
    @GetMapping("/my")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Get my applications with match scores and skills to improve")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal JobSeeker seeker) {
        return ResponseEntity.ok(applicationService.getSeekerApplications(seeker.getId()));
    }

    /** GET /api/applications/job/{jobId} */
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get ranked applicants for a job (Employer only)")
    public ResponseEntity<List<ApplicationResponse>> getJobApplicants(
            @PathVariable Long jobId,
            @AuthenticationPrincipal Employer employer) {
        return ResponseEntity.ok(applicationService.getJobApplicants(jobId, employer.getId()));
    }

    /** PATCH /api/applications/{id}/advance */
    @PatchMapping("/{id}/advance")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Advance application to next stage (Employer only)")
    public ResponseEntity<ApplicationResponse> advance(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer) {
        return ResponseEntity.ok(applicationService.advanceStage(id, employer.getId()));
    }

    /** PATCH /api/applications/{id}/reject */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Reject an applicant (Employer only)")
    public ResponseEntity<ApplicationResponse> reject(
            @PathVariable Long id,
            @AuthenticationPrincipal Employer employer,
            @RequestBody(required = false) Map<String, String> body) {
        String notes = (body != null) ? body.get("notes") : null;
        return ResponseEntity.ok(applicationService.rejectApplication(id, employer.getId(), notes));
    }
}

