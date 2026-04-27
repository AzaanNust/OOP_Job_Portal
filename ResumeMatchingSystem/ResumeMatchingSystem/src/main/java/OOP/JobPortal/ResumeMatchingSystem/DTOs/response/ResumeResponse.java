package OOP.JobPortal.ResumeMatchingSystem.DTOs.response;

import OOP.JobPortal.ResumeMatchingSystem.Entities.Resume;
import java.time.LocalDateTime;

/** Returned when viewing resume data. Includes all sections. */
public class ResumeResponse {
    private Long id;
    private Long jobSeekerId;
    private String seekerName;
    private String skills;
    private String workExperience;
    private String education;
    private String certifications;
    private String languages;
    private String projects;
    private String awards;
    private String volunteerWork;
    private String publications;
    private String referencesText;
    private Integer totalExperienceYears;
    private String portfolioUrl;
    private String linkedinUrl;
    private String githubUrl;
    private String aiGeneratedHtml;
    private LocalDateTime lastAiGeneratedAt;

    public ResumeResponse() {}

    public static ResumeResponse from(Resume r) {
        ResumeResponse dto = new ResumeResponse();
        dto.id                   = r.getId();
        dto.jobSeekerId          = r.getJobSeeker() != null ? r.getJobSeeker().getId() : null;
        dto.seekerName           = r.getJobSeeker() != null ? r.getJobSeeker().getFullName() : null;
        dto.skills               = r.getSkills();
        dto.workExperience       = r.getWorkExperience();
        dto.education            = r.getEducation();
        dto.certifications       = r.getCertifications();
        dto.languages            = r.getLanguages();
        dto.projects             = r.getProjects();
        dto.awards               = r.getAwards();
        dto.volunteerWork        = r.getVolunteerWork();
        dto.publications         = r.getPublications();
        dto.referencesText       = r.getReferencesText();
        dto.totalExperienceYears = r.getTotalExperienceYears();
        dto.portfolioUrl         = r.getPortfolioUrl();
        dto.linkedinUrl          = r.getLinkedinUrl();
        dto.githubUrl            = r.getGithubUrl();
        dto.aiGeneratedHtml      = r.getAiGeneratedHtml();
        dto.lastAiGeneratedAt    = r.getLastAiGeneratedAt();
        return dto;
    }

    public Long getId()                            { return id; }
    public void setId(Long v)                      { this.id = v; }
    public Long getJobSeekerId()                   { return jobSeekerId; }
    public void setJobSeekerId(Long v)             { this.jobSeekerId = v; }
    public String getSeekerName()                  { return seekerName; }
    public void setSeekerName(String v)            { this.seekerName = v; }
    public String getSkills()                      { return skills; }
    public void setSkills(String v)                { this.skills = v; }
    public String getWorkExperience()              { return workExperience; }
    public void setWorkExperience(String v)        { this.workExperience = v; }
    public String getEducation()                   { return education; }
    public void setEducation(String v)             { this.education = v; }
    public String getCertifications()              { return certifications; }
    public void setCertifications(String v)        { this.certifications = v; }
    public String getLanguages()                   { return languages; }
    public void setLanguages(String v)             { this.languages = v; }
    public String getProjects()                    { return projects; }
    public void setProjects(String v)              { this.projects = v; }
    public String getAwards()                      { return awards; }
    public void setAwards(String v)                { this.awards = v; }
    public String getVolunteerWork()               { return volunteerWork; }
    public void setVolunteerWork(String v)         { this.volunteerWork = v; }
    public String getPublications()                { return publications; }
    public void setPublications(String v)          { this.publications = v; }
    public String getReferencesText()              { return referencesText; }
    public void setReferencesText(String v)        { this.referencesText = v; }
    public Integer getTotalExperienceYears()       { return totalExperienceYears; }
    public void setTotalExperienceYears(Integer v) { this.totalExperienceYears = v; }
    public String getPortfolioUrl()                { return portfolioUrl; }
    public void setPortfolioUrl(String v)          { this.portfolioUrl = v; }
    public String getLinkedinUrl()                 { return linkedinUrl; }
    public void setLinkedinUrl(String v)           { this.linkedinUrl = v; }
    public String getGithubUrl()                   { return githubUrl; }
    public void setGithubUrl(String v)             { this.githubUrl = v; }
    public String getAiGeneratedHtml()             { return aiGeneratedHtml; }
    public void setAiGeneratedHtml(String v)       { this.aiGeneratedHtml = v; }
    public LocalDateTime getLastAiGeneratedAt()    { return lastAiGeneratedAt; }
    public void setLastAiGeneratedAt(LocalDateTime v) { this.lastAiGeneratedAt = v; }
}