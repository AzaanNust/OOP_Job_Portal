package OOP.JobPortal.ResumeMatchingSystem.DTOs.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/** Payload for saving or updating resume data — all fields optional */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResumeRequest {
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

    public ResumeRequest() {}

    public String  getSkills()                               { return skills; }
    //public void    setSkills(String v)                       { this.skills = v; }
    public String  getWorkExperience()                       { return workExperience; }
    //public void    setWorkExperience(String v)               { this.workExperience = v; }
    public String  getEducation()                            { return education; }
    //public void    setEducation(String v)                    { this.education = v; }
    public String  getCertifications()                       { return certifications; }
    //public void    setCertifications(String v)               { this.certifications = v; }
    public String  getLanguages()                            { return languages; }
    //public void    setLanguages(String v)                    { this.languages = v; }
    public String  getProjects()                             { return projects; }
    //public void    setProjects(String v)                     { this.projects = v; }
    public String  getAwards()                               { return awards; }
    //public void    setAwards(String v)                       { this.awards = v; }
    public String  getVolunteerWork()                        { return volunteerWork; }
    //public void    setVolunteerWork(String v)                { this.volunteerWork = v; }
    public String  getPublications()                         { return publications; }
    //public void    setPublications(String v)                 { this.publications = v; }
    public String  getReferencesText()                       { return referencesText; }
    //public void    setReferencesText(String v)               { this.referencesText = v; }
    public Integer getTotalExperienceYears()                 { return totalExperienceYears; }
    //public void    setTotalExperienceYears(Integer v)        { this.totalExperienceYears = v; }
    public String  getPortfolioUrl()                         { return portfolioUrl; }
    //public void    setPortfolioUrl(String v)                 { this.portfolioUrl = v; }
    public String  getLinkedinUrl()                          { return linkedinUrl; }
    //public void    setLinkedinUrl(String v)                  { this.linkedinUrl = v; }
    public String  getGithubUrl()                            { return githubUrl; }
    //public void    setGithubUrl(String v)                    { this.githubUrl = v; }
}