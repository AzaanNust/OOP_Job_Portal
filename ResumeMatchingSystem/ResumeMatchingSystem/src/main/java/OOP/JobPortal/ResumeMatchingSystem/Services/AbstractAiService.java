package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.entity.JobSeeker;
import OOP.JobPortal.ResumeMatchingSystem.entity.Resume;

public abstract class AbstractAiService {

    public abstract String generateResumeHtml(JobSeeker seeker, Resume resume);
}
