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

/**
 * ============================================================
 * JobService  –  Job Posting and Search
 * ============================================================
 *
 * WEEK 3 – Menu-driven system / Input → Process → Output:
 *   The job search feature is the "main menu" of the application.
 *   Input:   title, location, shift filters (all optional)
 *   Process: JPA repository applies the filters to a SQL query
 *   Output:  paginated list of matching open jobs
 *
 * WEEK 13 – Dependency Inversion Principle (DIP):
 *   JobService depends on JobListingRepository (an abstraction/interface),
 *   not on a concrete database implementation.
 *   Spring injects the concrete implementation at runtime.
 * ============================================================
 */
@Service
public class JobService extends AbstractJobService {

    @Autowired
    private JobListingRepository jobRepo;

    @Autowired
    private EmployerRepository employerRepo;

    /**
     * Posts a new job listing for the given employer.
     *
     * WEEK 10 – Composition: the job is owned by the employer.
     *   We verify the employer exists before creating the job.
     *
     * @param employerId the ID of the posting employer
     * @param request    the job data
     * @return           the created job as a response DTO
     * @throws ResourceNotFoundException if employer does not exist
     * @throws BusinessException         if deadline format is invalid
     */
    @Transactional
    public JobListingResponse postJob(Long employerId, JobListingRequest request) {

        Employer employer = employerRepo.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", employerId));

        // Create the JobListing entity using the composition constructor
        JobListing job = new JobListing(
                employer,
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getShiftType()
        );

        // Set all optional fields (using setters — Week 4)
        job.setRequiredSkills(request.getRequiredSkills());
        job.setPreferredSkills(request.getPreferredSkills());
        job.setMinExperienceYears(request.getMinExperienceYears());
        job.setMaxExperienceYears(request.getMaxExperienceYears());
        job.setMinSalary(request.getMinSalary());
        job.setMaxSalary(request.getMaxSalary());
        job.setJobType(request.getJobType());
        job.setOpenPositions(request.getOpenPositions());
        job.setStatus(JobStatus.OPEN);

        // Parse deadline if provided
        if (request.getDeadline() != null && !request.getDeadline().isBlank()) {
            try {
                job.setDeadline(LocalDateTime.parse(request.getDeadline()));
            } catch (Exception e) {
                throw new BusinessException(
                        "Invalid deadline format. Please use: 2024-12-31T23:59:59");
            }
        }

        job = jobRepo.save(job);

        return JobListingResponse.from(job);
    }

    /**
     * Searches open jobs with optional filters.
     *
     * WEEK 3 – Functionality: the core search feature.
     *   All parameters are optional — null means "no filter on this field".
     *   GET /api/jobs?location=Lahore&shift=NIGHT  → jobs in Lahore, night shift only
     *   GET /api/jobs                               → all open jobs
     *
     * @param title    partial job title (null = no filter)
     * @param location partial city name (null = no filter)
     * @param shift    shift type (null = no filter)
     * @param page     page number (0-based)
     * @param size     items per page
     * @return         paginated list of matching job responses
     */
    public Page<JobListingResponse> searchJobs(String title, String location,
                                               ShiftType shift, int page, int size) {

        // Normalize blank strings to null so the query works correctly
        String normalizedTitle    = (title    != null && title.isBlank())    ? null : title;
        String normalizedLocation = (location != null && location.isBlank()) ? null : location;

        // Sort by most recently posted first
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        // Delegate to the repository's custom JPQL query
        Page<JobListing> results = jobRepo.searchJobs(
                normalizedTitle, normalizedLocation, shift, pageable);

        // Convert each entity to a DTO using the static factory method
        return results.map(JobListingResponse::from);
    }

    /**
     * Returns a single job by ID.
     *
     * @param jobId the job listing ID
     * @return      the job response DTO
     * @throws ResourceNotFoundException if the job does not exist
     */
    public JobListingResponse getJobById(Long jobId) {
        JobListing job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId));
        return JobListingResponse.from(job);
    }

    /**
     * Returns all jobs posted by a specific employer.
     *
     * @param employerId the employer's ID
     * @return           list of the employer's job listings
     */


    /**
     * Updates an existing job listing.
     * Only the employer who originally posted the job can edit it.
     *
     * @param jobId      the job to update
     * @param employerId the employer attempting the update (for ownership check)
     * @param request    updated job data
     * @return           the updated job response DTO
     * @throws BusinessException if the employer does not own this job
     */
    @Transactional
    public JobListingResponse updateJob(Long jobId, Long employerId,
                                        JobListingRequest request) {

        JobListing job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId));

        // Ownership check — you can only edit your own jobs
        if (!job.getEmployer().getId().equals(employerId)) {
            throw new BusinessException("You can only edit your own job listings.");
        }

        // Update the fields using setters (Week 4)
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

    /**
     * Closes a job listing — it will no longer accept applications.
     * Uses the business method JobListing.closeJob() from the entity.
     *
     * @param jobId      the job to close
     * @param employerId the employer attempting to close (ownership check)
     * @return           the updated job response DTO
     * @throws BusinessException if the employer does not own this job
     */
    @Transactional
    public JobListingResponse closeJob(Long jobId, Long employerId) {

        JobListing job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId));

        if (!job.getEmployer().getId().equals(employerId)) {
            throw new BusinessException("You can only close your own job listings.");
        }

        // Call the entity's business method (encapsulation — the entity manages its own state)
        job.closeJob();

        return JobListingResponse.from(jobRepo.save(job));
    }
}
