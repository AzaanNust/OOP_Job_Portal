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
    public JobListingResponse postJob(Long employerId, JobListingRequest request) {

        Employer employer = employerRepo.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", employerId));

        JobListing job = new JobListing(
                employer,
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getShiftType()
        );

        job.setRequiredSkills(request.getRequiredSkills());
        job.setPreferredSkills(request.getPreferredSkills());
        job.setMinExperienceYears(request.getMinExperienceYears());
        job.setMaxExperienceYears(request.getMaxExperienceYears());
        job.setMinSalary(request.getMinSalary());
        job.setMaxSalary(request.getMaxSalary());
        job.setJobType(request.getJobType());
        job.setOpenPositions(request.getOpenPositions());
        job.setStatus(JobStatus.OPEN);

        if (request.getDeadline() != null && !request.getDeadline().isBlank()) {
            try {
                job.setDeadline(LocalDateTime.parse(request.getDeadline()));
            } catch (Exception e) {
                throw new BusinessException(
                        "Invalid deadline format. Use: 2024-12-31T23:59:59");
            }
        }

        return JobListingResponse.from(jobRepo.save(job));
    }

    /**
     * Searches open jobs with optional filters.
     * Returns a plain List — avoids Spring PageImpl serialization issues
     * which caused Gson on Android to silently return null content.
     */
    public List<JobListingResponse> searchJobs(String title, String location, ShiftType shift) {

        String normalizedTitle    = (title    != null && title.isBlank())    ? null : title;
        String normalizedLocation = (location != null && location.isBlank()) ? null : location;

        return jobRepo.searchJobsList(normalizedTitle, normalizedLocation, shift)
                .stream()
                .map(JobListingResponse::from)
                .collect(Collectors.toList());
    }

    public JobListingResponse getJobById(Long jobId) {
        return JobListingResponse.from(
                jobRepo.findById(jobId)
                        .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId))
        );
    }

    public List<JobListingResponse> getJobsByEmployer(Long employerId) {
        return jobRepo.findByEmployerIdOrderByCreatedAtDesc(employerId)
                .stream()
                .map(JobListingResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobListingResponse updateJob(Long jobId, Long employerId, JobListingRequest request) {

        JobListing job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId));

        if (!job.getEmployer().getId().equals(employerId))
            throw new BusinessException("You can only edit your own job listings.");

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setShiftType(request.getShiftType());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setPreferredSkills(request.getPreferredSkills());
        job.setMinExperienceYears(request.getMinExperienceYears());
        job.setMinSalary(request.getMinSalary());
        job.setMaxSalary(request.getMaxSalary());
        job.setJobType(request.getJobType());

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

    @Transactional
    public JobListingResponse reactivateJob(Long jobId, Long employerId) {

        JobListing job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId));

        if (!job.getEmployer().getId().equals(employerId))
            throw new BusinessException("You can only reactivate your own job listings.");

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