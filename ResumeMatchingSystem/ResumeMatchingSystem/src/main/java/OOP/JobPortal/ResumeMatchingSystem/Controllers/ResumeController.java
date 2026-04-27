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

@RestController
@RequestMapping("/api/resume")
@Tag(name = "Resume", description = "Manage resume data and PDF download")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Get the logged-in seeker's resume")
    public ResponseEntity<ResumeResponse> getMyResume(
            @AuthenticationPrincipal JobSeeker seeker) {
        return ResponseEntity.ok(resumeService.getBySeekerId(seeker.getId()));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Save or update resume data")
    public ResponseEntity<ResumeResponse> saveResume(
            @AuthenticationPrincipal JobSeeker seeker,
            @RequestBody ResumeRequest request) {
        return ResponseEntity.ok(resumeService.saveOrUpdate(seeker.getId(), request));
    }

    @GetMapping("/download")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Download resume as a professional PDF (no AI needed)")
    public ResponseEntity<byte[]> downloadMyResume(
            @AuthenticationPrincipal JobSeeker seeker) {
        byte[] pdfBytes = resumeService.getResumePdfBytes(seeker.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"resume_" + seeker.getId() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/{seekerId}/download")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Employer: download an applicant's resume PDF")
    public ResponseEntity<byte[]> downloadApplicantResume(
            @PathVariable Long seekerId) {
        byte[] pdfBytes = resumeService.getResumePdfBytes(seekerId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"applicant_resume_" + seekerId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}