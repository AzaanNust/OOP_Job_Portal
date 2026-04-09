package OOP.JobPortal.ResumeMatchingSystem.DTOs.request;
/** Payload for saving or updating resume data */
public class ResumeRequest {
    private String skills;
    private String workExperience;
    private String education;
    private String certifications;
    private String languages;
    private String portfolioUrl;
    private String linkedinUrl;
    private String githubUrl;
    public ResumeRequest() {}
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
}
