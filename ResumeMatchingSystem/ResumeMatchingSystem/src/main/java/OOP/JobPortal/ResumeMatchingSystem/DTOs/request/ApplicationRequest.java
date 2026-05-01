package OOP.JobPortal.ResumeMatchingSystem.DTOs.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/** Payload for submitting a job application */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ApplicationRequest {
    private Long jobListingId;
    private String coverLetter;
//    public ApplicationRequest() {}
    public Long getJobListingId() { return jobListingId; }
//    public void setJobListingId(Long jobListingId) { this.jobListingId = jobListingId; }
    public String getCoverLetter() { return coverLetter; }
//    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
}
