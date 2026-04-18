package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.dto.request.ApplicationRequest;
import OOP.JobPortal.ResumeMatchingSystem.dto.response.ApplicationResponse;

import java.util.List;

public abstract class AbstractApplicationService {

    public abstract ApplicationResponse apply(Long seekerId, ApplicationRequest request);

    public abstract List<ApplicationResponse> getSeekerApplications(Long seekerId);

    public abstract List<ApplicationResponse> getJobApplicants(Long jobId, Long employerId);

    public abstract ApplicationResponse advanceStage(Long applicationId, Long employerId);

    public abstract ApplicationResponse rejectApplication(Long applicationId, Long employerId, String notes);
}