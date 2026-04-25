package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.Entities.JobSeeker;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Resume;

public abstract class AbstractAiService {

    public abstract String generateResumeHtml(JobSeeker seeker, Resume resume);
}
