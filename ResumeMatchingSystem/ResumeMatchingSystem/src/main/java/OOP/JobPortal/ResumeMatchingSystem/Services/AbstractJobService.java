package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.dto.request.JobListingRequest;
import OOP.JobPortal.ResumeMatchingSystem.dto.response.JobListingResponse;
import OOP.JobPortal.ResumeMatchingSystem.enums.ShiftType;
import org.springframework.data.domain.Page;

public abstract class AbstractJobService {

    public abstract JobListingResponse postJob(Long employerId, JobListingRequest request);

    public abstract Page<JobListingResponse> searchJobs(String title, String location,
                                                        ShiftType shift, int page, int size);

    public abstract JobListingResponse getJobById(Long jobId);

    public abstract JobListingResponse updateJob(Long jobId, Long employerId,
                                                 JobListingRequest request);

    public abstract JobListingResponse closeJob(Long jobId, Long employerId);
}