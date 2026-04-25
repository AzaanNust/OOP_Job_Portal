package OOP.JobPortal.ResumeMatchingSystem.Services.impl;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.ApplicationRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.response.ApplicationResponse;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Application;
import OOP.JobPortal.ResumeMatchingSystem.Entities.JobListing;
import OOP.JobPortal.ResumeMatchingSystem.Entities.JobSeeker;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Resume;
import OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus;
import OOP.JobPortal.ResumeMatchingSystem.Exceptions.BusinessException;
import OOP.JobPortal.ResumeMatchingSystem.Exceptions.InvalidStageTransitionException;
import OOP.JobPortal.ResumeMatchingSystem.Exceptions.ResourceNotFoundException;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.ApplicationRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.JobListingRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.JobSeekerRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.ResumeRepository;
import OOP.JobPortal.ResumeMatchingSystem.Services.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 * ApplicationService  –  Job Application Pipeline Management
 * ============================================================
 *
 * WEEK 8 – Interfaces / Dependency Inversion:
 *   This service depends on MatchingStrategy (an interface), not
 *   on SkillBasedMatcher directly. Spring injects the implementation.
 *   This means you can swap the algorithm without touching this file.
 *
 * WEEK 12 – Exception Handling:
 *   advanceStage() calls application.advanceStage() which throws a
 *   CHECKED exception (InvalidStageTransitionException).
 *   This service catches it and converts it to a BusinessException
 *   (unchecked) that GlobalExceptionHandler will handle.
 *
 * WEEK 11 – Association:
 *   apply() links a JobSeeker to a JobListing via an Application.
 *   Both entities can independently exist.
 *
 * WEEK 7 – Polymorphism:
 *   Notifications are triggered using NotificationService, which
 *   internally calls the polymorphic send() on notification objects.
 * ============================================================
 */
@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepo;

    @Autowired
    private JobSeekerRepository seekerRepo;

    @Autowired
    private JobListingRepository jobRepo;

    @Autowired
    private ResumeRepository resumeRepo;

    /**
     * WEEK 8 / WEEK 13 – Dependency Inversion Principle:
     * We depend on the MatchingStrategy INTERFACE, not a concrete class.
     * Spring automatically injects SkillBasedMatcher at runtime.
     */
    @Autowired
    private MatchingStrategy matchingStrategy;

    @Autowired
    private NotificationService notificationService;

    /**
     * Submits a job application for a seeker.
     *
     * STEPS:
     *   1. Verify job is still OPEN
     *   2. Prevent duplicate applications
     *   3. Verify seeker has a resume
     *   4. Calculate match score (via MatchingStrategy interface)
     *   5. Get skills to improve
     *   6. Save the Application entity
     *   7. Send confirmation notifications
     *
     * WEEK 11 – Association: creates the Application bridge entity
     * WEEK 8  – Interface:   matchingStrategy is called polymorphically
     *
     * @param seekerId the ID of the applying job seeker
     * @param request  application payload (jobListingId, coverLetter)
     * @return         the created application as a response DTO
     */
    @Transactional
    public ApplicationResponse apply(Long seekerId, ApplicationRequest request) {

        JobSeeker seeker = seekerRepo.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker", seekerId));

        JobListing job = jobRepo.findById(request.getJobListingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "JobListing", request.getJobListingId()));

        // WEEK 12 – Business rule validation with custom exceptions
        if (job.getStatus() != JobStatus.OPEN) {
            throw new BusinessException(
                    "This job is no longer accepting applications. Status: " + job.getStatus());
        }

        if (applicationRepo.existsByJobSeekerIdAndJobListingId(
                seekerId, request.getJobListingId())) {
            throw new BusinessException(
                    "You have already applied for the job: " + job.getTitle());
        }

        // A resume is required to apply
        Resume resume = resumeRepo.findByJobSeekerId(seekerId)
                .orElseThrow(() -> new BusinessException(
                        "Please create your resume before applying for jobs."));

        // WEEK 8 – Interface call (runtime polymorphism / Strategy pattern)
        double matchScore            = matchingStrategy.calculateScore(resume, job);
        List<String> skillsToImprove = matchingStrategy.getSkillsToImprove(resume, job);

        // WEEK 11 – Association: create the bridge entity linking seeker + job
        Application application = new Application(seeker, job, matchScore);
        application.setCoverLetter(request.getCoverLetter());

        // Store skills as comma-separated string
        if (!skillsToImprove.isEmpty()) {
            application.setSkillsToImprove(String.join(", ", skillsToImprove));
        }

        application = applicationRepo.save(application);

        // WEEK 7 – Polymorphism: triggers both Email + InApp notifications
        notificationService.notifyApplicationReceived(
                seeker.getEmail(), seeker.getFullName(), job.getTitle());

        return ApplicationResponse.from(application);
    }

    /**
     * Returns all applications submitted by a seeker.
     *
     * @param seekerId the seeker's ID
     * @return         list of applications with match scores and skills to improve
     */
    public List<ApplicationResponse> getSeekerApplications(Long seekerId) {
        return applicationRepo.findByJobSeekerIdOrderByAppliedAtDesc(seekerId)
                .stream()
                .map(ApplicationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Returns all applicants for a job, sorted by match score (highest first).
     * Only the employer who posted the job can call this.
     *
     * @param jobId      the job listing ID
     * @param employerId the employer requesting the list (ownership check)
     * @return           ranked list of applicants
     * @throws BusinessException if employer does not own this job
     */
    public List<ApplicationResponse> getJobApplicants(Long jobId, Long employerId) {

        JobListing job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing", jobId));

        if (!job.getEmployer().getId().equals(employerId)) {
            throw new BusinessException("You can only view applicants for your own jobs.");
        }

        return applicationRepo.findByJobListingIdOrderByMatchScoreDesc(jobId)
                .stream()
                .map(ApplicationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Advances an application to the next pipeline stage.
     *
     * WEEK 12 – Exception Handling (checked exception):
     *   application.advanceStage() throws InvalidStageTransitionException.
     *   We catch it here and convert to BusinessException (unchecked)
     *   so GlobalExceptionHandler can return it as HTTP 400.
     *
     * WEEK 7 – Polymorphism:
     *   NotificationService triggers polymorphic notification delivery.
     *
     * @param applicationId the application to advance
     * @param employerId    the employer making the decision (ownership check)
     * @return              the updated application DTO
     * @throws BusinessException if stage transition is invalid or ownership fails
     */
    @Transactional
    public ApplicationResponse advanceStage(Long applicationId, Long employerId) {

        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        if (!application.getJobListing().getEmployer().getId().equals(employerId)) {
            throw new BusinessException("You can only manage applications for your own jobs.");
        }

        try {
            // WEEK 12 – Calling a method that throws a checked exception
            application.advanceStage();

        } catch (InvalidStageTransitionException e) {
            // WEEK 12 – Converting checked exception to unchecked for the controller
            throw new BusinessException(e.getMessage());
        }

        application = applicationRepo.save(application);

        // Notify the seeker about the stage change
        notificationService.notifyApplicationStageChange(
                application.getJobSeeker().getEmail(),
                application.getJobSeeker().getFullName(),
                application.getJobListing().getTitle(),
                application.getStage()
        );

        return ApplicationResponse.from(application);
    }

    /**
     * Rejects an applicant.
     *
     * WEEK 12 – Exception Handling:
     *   application.reject() also throws InvalidStageTransitionException.
     *
     * @param applicationId the application to reject
     * @param employerId    the employer making the decision
     * @param notes         optional employer notes for the rejection
     * @return              the updated application DTO
     */
    @Transactional
    public ApplicationResponse rejectApplication(Long applicationId, Long employerId,
                                                 String notes) {

        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        if (!application.getJobListing().getEmployer().getId().equals(employerId)) {
            throw new BusinessException("You can only manage applications for your own jobs.");
        }

        try {
            application.reject();
        } catch (InvalidStageTransitionException e) {
            throw new BusinessException(e.getMessage());
        }

        if (notes != null && !notes.isBlank()) {
            application.setEmployerNotes(notes);
        }

        application = applicationRepo.save(application);

        // Notify the seeker of the rejection
        notificationService.notifyApplicationStageChange(
                application.getJobSeeker().getEmail(),
                application.getJobSeeker().getFullName(),
                application.getJobListing().getTitle(),
                application.getStage()
        );

        return ApplicationResponse.from(application);
    }
}
