package OOP.JobPortal.ResumeMatchingSystem.DTOs.response;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Application;
import OOP.JobPortal.ResumeMatchingSystem.Enums.AppStage;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/** Returned when viewing a job application */
public class ApplicationResponse {
    private Long id;
    private Long jobListingId;
    private String jobTitle;
    private String companyName;
    private String jobLocation;
    private double matchScore;
    private List<String> skillsToImprove;
    private AppStage stage;
    private String coverLetter;
    private String employerNotes;
    private LocalDateTime appliedAt;
    private Long seekerId;
    private String seekerName;
    private String seekerEmail;
    public ApplicationResponse() {}
    /** Factory method: converts an Application entity to a response DTO */
    public static ApplicationResponse from(Application app) {
        ApplicationResponse r = new ApplicationResponse();
        r.setId(app.getId());
        r.setJobListingId(app.getJobListing().getId());
        r.setJobTitle(app.getJobListing().getTitle());
        r.setCompanyName(app.getJobListing().getEmployer().getCompanyName());
        r.setJobLocation(app.getJobListing().getLocation());
        r.setMatchScore(app.getMatchScore());
        if (app.getSkillsToImprove() != null && !app.getSkillsToImprove().isBlank()) {
            r.setSkillsToImprove(Arrays.asList(app.getSkillsToImprove().split(",")));
        } else {
            r.setSkillsToImprove(Collections.emptyList());
        }
        r.setStage(app.getStage());
        r.setCoverLetter(app.getCoverLetter());
        r.setEmployerNotes(app.getEmployerNotes());
        r.setAppliedAt(app.getAppliedAt());
        r.setSeekerId(app.getJobSeeker().getId());
        r.setSeekerName(app.getJobSeeker().getFullName());
        r.setSeekerEmail(app.getJobSeeker().getEmail());
        return r;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getJobListingId() { return jobListingId; }
    public void setJobListingId(Long jobListingId) { this.jobListingId = jobListingId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getJobLocation() { return jobLocation; }
    public void setJobLocation(String jobLocation) { this.jobLocation = jobLocation; }
    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
    public List<String> getSkillsToImprove() { return skillsToImprove; }
    public void setSkillsToImprove(List<String> skillsToImprove) { this.skillsToImprove = skillsToImprove; }
    public AppStage getStage() { return stage; }
    public void setStage(AppStage stage) { this.stage = stage; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public String getEmployerNotes() { return employerNotes; }
    public void setEmployerNotes(String employerNotes) { this.employerNotes = employerNotes; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public Long getSeekerId() { return seekerId; }
    public void setSeekerId(Long seekerId) { this.seekerId = seekerId; }
    public String getSeekerName() { return seekerName; }
    public void setSeekerName(String seekerName) { this.seekerName = seekerName; }
    public String getSeekerEmail() { return seekerEmail; }
    public void setSeekerEmail(String seekerEmail) { this.seekerEmail = seekerEmail; }
}
