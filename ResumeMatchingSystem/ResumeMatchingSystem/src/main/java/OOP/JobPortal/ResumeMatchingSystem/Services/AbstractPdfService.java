package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.Entities.Resume;

public abstract class AbstractPdfService {

    public abstract byte[] generateResumePdf(Resume resume);
}