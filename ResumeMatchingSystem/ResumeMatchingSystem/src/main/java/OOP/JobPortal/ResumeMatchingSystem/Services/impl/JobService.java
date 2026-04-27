package OOP.JobPortal.ResumeMatchingSystem.Services.impl;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.JobListingRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.response.JobListingResponse;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Employer;
import OOP.JobPortal.ResumeMatchingSystem.Entities.JobListing;
import OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus;
import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;
import OOP.JobPortal.ResumeMatchingSystem.Exceptions.BusinessException;
import OOP.JobPortal.ResumeMatchingSystem.Exceptions.ResourceNotFoundException;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.EmployerRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.JobListingRepository;
import OOP.JobPortal.ResumeMatchingSystem.Services.AbstractJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService extends AbstractJobService {

    @Autowired private JobListingRepository jobRepo;
    @Autowired private EmployerRepository   employerRepo;

    @Transactional
    public JobListingResponse postJob(Long employerId, JobListingRequest req) {
        Employer employer = employerRepo.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", employerId));
        JobListing job = new JobListing(employer, req.getTitle(),
                req.getDescription(), req.getLocation(), req.getShiftType());
        job.setRequiredSkills(req.getRequiredSkills());
        job.setPreferredSkills(req.getPreferredSkills());
        job.setMinExperienceYears(req.getMinExperienceYears());
        job.setMaxExperienceYears(req.getMaxExperienceYears());
        job.setMinSalary(req.getMinSalary());
        job.setMaxSalary(req.getMaxSalary());
        job.setJobType(req.getJobType());
        job.setOpenPositions(req.getOpenPositions());
        job.setStatus(JobStatus.OPEN);
        if (req.getDeadline() != null && !req.getDeadline().isBlank()) {
            try { job.setDeadline(LocalDateTime.parse(req.getDeadline())); }
            catch (Exception e) { throw new BusinessException("Invalid deadline. Use: 2024-12-31T23:59:59"); }
        }
        return JobListingResponse.from(jobRepo.save(job));
    }

    /**
     * Searches open jobs. When title/location/shift are all null -> returns ALL open jobs.
     * Title search is partial and case-insensitive.
     */
    public Page<JobListingResponse> searchJobs(String title, String location,
                                               ShiftType shift, int page, int size) {
        String t = (title != null && !title.isBlank())       ? title.trim()    : null;
        String l = (location != null && !location.isBlank()) ? location.trim() : null;
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jobRepo.searchJobs(t, l, shift, pageable).map(JobListingResponse::from);
    }

    public JobListingResponse getJobById(Long jobId) {
        return JobListingResponse.from(jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId)));
    }

    public List<JobListingResponse> getJobsByEmployer(Long employerId) {
        return jobRepo.findByEmployerIdOrderByCreatedAtDesc(employerId)
                .stream().map(JobListingResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public JobListingResponse updateJob(Long jobId, Long employerId, JobListingRequest req) {
        JobListing job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId));
        if (!job.getEmployer().getId().equals(employerId))
            throw new BusinessException("You can only edit your own job listings.");
        job.setTitle(req.getTitle());
        job.setDescription(req.getDescription());
        job.setLocation(req.getLocation());
        job.setShiftType(req.getShiftType());
        job.setRequiredSkills(req.getRequiredSkills());
        job.setPreferredSkills(req.getPreferredSkills());
        job.setMinExperienceYears(req.getMinExperienceYears());
        job.setMinSalary(req.getMinSalary());
        job.setMaxSalary(req.getMaxSalary());
        job.setJobType(req.getJobType());
        return JobListingResponse.from(jobRepo.save(job));
    }

    @Transactional
    public JobListingResponse closeJob(Long jobId, Long employerId) {
        JobListing job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId));
        if (!job.getEmployer().getId().equals(employerId))
            throw new BusinessException("You can only close your own job listings.");
        job.closeJob();
        return JobListingResponse.from(jobRepo.save(job));
    }

    /** Reactivates a CLOSED job back to OPEN */
    @Transactional
    public JobListingResponse reactivateJob(Long jobId, Long employerId) {
        JobListing job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId));
        if (!job.getEmployer().getId().equals(employerId))
            throw new BusinessException("You can only reactivate your own job listings.");
        if (job.getStatus() == JobStatus.OPEN)
            throw new BusinessException("Job is already open.");
        job.setStatus(JobStatus.OPEN);
        return JobListingResponse.from(jobRepo.save(job));
    }

    @Transactional
    public void deleteJob(Long jobId, Long employerId) {
        JobListing job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId));
        if (!job.getEmployer().getId().equals(employerId))
            throw new BusinessException("You can only delete your own job listings.");
        jobRepo.delete(job);
    }
}