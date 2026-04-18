package OOP.JobPortal.ResumeMatchingSystem.Controllers;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.ResumeRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.response.ResumeResponse;
import OOP.JobPortal.ResumeMatchingSystem.Entities.JobSeeker;
import OOP.JobPortal.ResumeMatchingSystem.Services.impl.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * ResumeController – resume CRUD, AI generation, PDF download.
 * WEEK 3 – File handling: /download endpoint streams a PDF file to the browser.
 */
@RestController
@RequestMapping("/api/resume")
@Tag(name = "Resume", description = "Resume management, AI generation, and PDF download")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    /** GET /api/resume/my */
    @GetMapping("/my")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Get the logged-in seeker's resume")
    public ResponseEntity<ResumeResponse> getMyResume(
            @AuthenticationPrincipal JobSeeker seeker) {
        return ResponseEntity.ok(resumeService.getBySeekerId(seeker.getId()));
    }

    /** POST /api/resume/save */
    @PostMapping("/save")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Save or update resume data")
    public ResponseEntity<ResumeResponse> saveResume(
            @AuthenticationPrincipal JobSeeker seeker,
            @RequestBody ResumeRequest request) {
        return ResponseEntity.ok(resumeService.saveOrUpdate(seeker.getId(), request));
    }

    /** POST /api/resume/generate-ai */
    @PostMapping("/generate-ai")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Generate a professional resume using Claude AI")
    public ResponseEntity<ResumeResponse> generateWithAI(
            @AuthenticationPrincipal JobSeeker seeker) {
        return ResponseEntity.ok(resumeService.generateWithAI(seeker.getId()));
    }

    /**
     * GET /api/resume/download
     * WEEK 3 – File Handling: streams a PDF file to the browser.
     * Content-Disposition: attachment tells the browser to download rather than display.
     */
    @GetMapping("/download")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Download resume as PDF")
    public ResponseEntity<byte[]> downloadMyResume(
            @AuthenticationPrincipal JobSeeker seeker) {
        byte[] pdf      = resumeService.getResumePdfBytes(seeker.getId());
        String filename = seeker.getFullName().replace(" ", "_") + "_Resume.pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    /** GET /api/resume/{seekerId}/download – employer downloads an applicant's resume */
    @GetMapping("/{seekerId}/download")
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @Operation(summary = "Download an applicant's resume as PDF (Employer/Admin only)")
    public ResponseEntity<byte[]> downloadApplicantResume(@PathVariable Long seekerId) {
        byte[] pdf = resumeService.getResumePdfBytes(seekerId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"applicant_" + seekerId + "_resume.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
