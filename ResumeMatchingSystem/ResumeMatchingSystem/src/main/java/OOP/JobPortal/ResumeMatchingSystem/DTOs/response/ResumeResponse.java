package OOP.JobPortal.ResumeMatchingSystem.DTOs.response;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Resume;
import java.time.LocalDateTime;
/** Returned when viewing or generating a resume */
public class ResumeResponse {
    private Long id;
    private Long jobSeekerId;
    private String seekerName;
    private String skills;
    private String workExperience;
    private String education;
    private String certifications;
    private String languages;
    private String portfolioUrl;
    private String linkedinUrl;
    private String githubUrl;
    private String aiGeneratedHtml;
    private LocalDateTime lastAiGeneratedAt;
    public ResumeResponse() {}
    /** Factory method: converts a Resume entity to a response DTO */
    public static ResumeResponse from(Resume resume) {
        ResumeResponse r = new ResumeResponse();
        r.setId(resume.getId());
        r.setJobSeekerId(resume.getJobSeeker().getId());
        r.setSeekerName(resume.getJobSeeker().getFullName());
        r.setSkills(resume.getSkills());
        r.setWorkExperience(resume.getWorkExperience());
        r.setEducation(resume.getEducation());
        r.setCertifications(resume.getCertifications());
        r.setLanguages(resume.getLanguages());
        r.setPortfolioUrl(resume.getPortfolioUrl());
        r.setLinkedinUrl(resume.getLinkedinUrl());
        r.setGithubUrl(resume.getGithubUrl());
        r.setAiGeneratedHtml(resume.getAiGeneratedHtml());
        r.setLastAiGeneratedAt(resume.getLastAiGeneratedAt());
        return r;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getJobSeekerId() { return jobSeekerId; }
    public void setJobSeekerId(Long jobSeekerId) { this.jobSeekerId = jobSeekerId; }
    public String getSeekerName() { return seekerName; }
    public void setSeekerName(String seekerName) { this.seekerName = seekerName; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getWorkExperience() { return workExperience; }
    public void setWorkExperience(String workExperience) { this.workExperience = workExperience; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getAiGeneratedHtml() { return aiGeneratedHtml; }
    public void setAiGeneratedHtml(String aiGeneratedHtml) { this.aiGeneratedHtml = aiGeneratedHtml; }
    public LocalDateTime getLastAiGeneratedAt() { return lastAiGeneratedAt; }
    public void setLastAiGeneratedAt(LocalDateTime lastAiGeneratedAt) { this.lastAiGeneratedAt = lastAiGeneratedAt; }
}
