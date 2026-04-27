package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.JobListingRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.response.JobListingResponse;
import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;
import org.springframework.data.domain.Page;

import java.util.List;

public abstract class AbstractJobService {

    public abstract JobListingResponse postJob(Long employerId, JobListingRequest request);

    public abstract Page<JobListingResponse> searchJobs(String title, String location,
                                                        ShiftType shift, int page, int size);

    public abstract JobListingResponse getJobById(Long jobId);

    public abstract List<JobListingResponse> getJobsByEmployer(Long employerId);

    public abstract JobListingResponse updateJob(Long jobId, Long employerId,
                                                 JobListingRequest request);

    public abstract JobListingResponse closeJob(Long jobId, Long employerId);

    /** Reactivates a closed job back to OPEN status */
    public abstract JobListingResponse reactivateJob(Long jobId, Long employerId);

    /** Permanently deletes a job listing */
    public abstract void deleteJob(Long jobId, Long employerId);
}