package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.entity.Resume;

public abstract class AbstractPdfService {

    public abstract byte[] generateResumePdf(Resume resume);
}