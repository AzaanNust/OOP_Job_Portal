package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.ResumeRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.response.ResumeResponse;

public abstract class AbstractResumeService {

    public abstract ResumeResponse saveOrUpdate(Long seekerId, ResumeRequest request);

    public abstract ResumeResponse generateWithAI(Long seekerId);

    public abstract byte[] getResumePdfBytes(Long seekerId);

    public abstract ResumeResponse getBySeekerId(Long seekerId);
}