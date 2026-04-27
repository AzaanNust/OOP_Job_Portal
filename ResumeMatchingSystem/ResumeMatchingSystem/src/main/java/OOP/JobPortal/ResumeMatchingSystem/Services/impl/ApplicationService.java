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
import OOP.JobPortal.ResumeMatchingSystem.Services.AbstractApplicationService;
import OOP.JobPortal.ResumeMatchingSystem.Services.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService extends AbstractApplicationService {

    @Autowired private ApplicationRepository applicationRepo;
    @Autowired private JobSeekerRepository   seekerRepo;
    @Autowired private JobListingRepository  jobRepo;
    @Autowired private ResumeRepository      resumeRepo;
    @Autowired private MatchingStrategy      matchingStrategy;
    @Autowired private NotificationService   notificationService;

    @Transactional
    public ApplicationResponse apply(Long seekerId, ApplicationRequest request) {

        JobSeeker seeker = seekerRepo.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker", seekerId));

        JobListing job = jobRepo.findById(request.getJobListingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "JobListing", request.getJobListingId()));

        if (job.getStatus() != JobStatus.OPEN) {
            throw new BusinessException(
                    "This job is no longer accepting applications. Status: " + job.getStatus());
        }

        if (applicationRepo.existsByJobSeekerIdAndJobListingId(
                seekerId, request.getJobListingId())) {
            throw new BusinessException(
                    "You have already applied for the job: " + job.getTitle());
        }

        Resume resume = resumeRepo.findByJobSeekerId(seekerId)
                .orElseThrow(() -> new BusinessException(
                        "Please create your resume before applying for jobs."));

        double matchScore            = matchingStrategy.calculateScore(resume, job);
        List<String> skillsToImprove = matchingStrategy.getSkillsToImprove(resume, job);

        Application application = new Application(seeker, job, matchScore);
        application.setCoverLetter(request.getCoverLetter());

        if (!skillsToImprove.isEmpty()) {
            application.setSkillsToImprove(String.join(", ", skillsToImprove));
        }

        application = applicationRepo.save(application);

        // Notify the seeker that their application was received
        notificationService.notifyApplicationReceived(
                seeker.getEmail(), seeker.getFullName(), job.getTitle());

        // Notify the employer that a new applicant has applied to their job
        notificationService.notifyNewApplication(
                job.getEmployer().getEmail(),
                job.getEmployer().getCompanyName(),
                seeker.getFullName(),
                job.getTitle());

        return ApplicationResponse.from(application);
    }

    public List<ApplicationResponse> getSeekerApplications(Long seekerId) {
        return applicationRepo.findByJobSeekerIdOrderByAppliedAtDesc(seekerId)
                .stream()
                .map(ApplicationResponse::from)
                .collect(Collectors.toList());
    }

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

    @Transactional
    public ApplicationResponse advanceStage(Long applicationId, Long employerId) {

        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        if (!application.getJobListing().getEmployer().getId().equals(employerId)) {
            throw new BusinessException("You can only manage applications for your own jobs.");
        }

        try {
            application.advanceStage();
        } catch (InvalidStageTransitionException e) {
            throw new BusinessException(e.getMessage());
        }

        application = applicationRepo.save(application);

        notificationService.notifyApplicationStageChange(
                application.getJobSeeker().getEmail(),
                application.getJobSeeker().getFullName(),
                application.getJobListing().getTitle(),
                application.getStage());

        return ApplicationResponse.from(application);
    }

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

        notificationService.notifyApplicationStageChange(
                application.getJobSeeker().getEmail(),
                application.getJobSeeker().getFullName(),
                application.getJobListing().getTitle(),
                application.getStage());

        return ApplicationResponse.from(application);
    }
}