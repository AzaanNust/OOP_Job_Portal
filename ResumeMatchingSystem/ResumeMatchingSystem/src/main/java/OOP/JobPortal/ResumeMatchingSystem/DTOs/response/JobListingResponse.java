package OOP.JobPortal.ResumeMatchingSystem.DTOs.response;
import OOP.JobPortal.ResumeMatchingSystem.Entities.JobListing;
import OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus;
import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;
/** Returned when viewing a job listing. Includes employer info and applicant count. */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class JobListingResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private ShiftType shiftType;
    private String requiredSkills;
    private String preferredSkills;
    private int minExperienceYears;
    private Double minSalary;
    private Double maxSalary;
    private String jobType;
    private int openPositions;
    private JobStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private Long employerId;
    private String companyName;
    private String companyLocation;
    private String industry;
    private int totalApplicants;
    public JobListingResponse() {}
    /** Factory method: converts a JobListing entity to a response DTO */
    public static JobListingResponse from(JobListing job) {
        JobListingResponse r = new JobListingResponse();
        r.setId(job.getId());
        r.setTitle(job.getTitle());
        r.setDescription(job.getDescription());
        r.setLocation(job.getLocation());
        r.setShiftType(job.getShiftType());
        r.setRequiredSkills(job.getRequiredSkills());
        r.setPreferredSkills(job.getPreferredSkills());
        r.setMinExperienceYears(job.getMinExperienceYears());
        r.setMinSalary(job.getMinSalary());
        r.setMaxSalary(job.getMaxSalary());
        r.setJobType(job.getJobType());
        r.setOpenPositions(job.getOpenPositions());
        r.setStatus(job.getStatus());
        r.setCreatedAt(job.getCreatedAt());
        r.setDeadline(job.getDeadline());
        r.setEmployerId(job.getEmployer().getId());
        r.setCompanyName(job.getEmployer().getCompanyName());
        r.setCompanyLocation(job.getEmployer().getCompanyLocation());
        r.setIndustry(job.getEmployer().getIndustry());
        r.setTotalApplicants(job.getApplications().size());
        return r;
    }
    //public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    //public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    //public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    //public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    //public ShiftType getShiftType() { return shiftType; }
    public void setShiftType(ShiftType shiftType) { this.shiftType = shiftType; }
    //public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }
    //public String getPreferredSkills() { return preferredSkills; }
    public void setPreferredSkills(String preferredSkills) { this.preferredSkills = preferredSkills; }
    //public int getMinExperienceYears() { return minExperienceYears; }
    public void setMinExperienceYears(int minExperienceYears) { this.minExperienceYears = minExperienceYears; }
    //public Double getMinSalary() { return minSalary; }
    public void setMinSalary(Double minSalary) { this.minSalary = minSalary; }
    //public Double getMaxSalary() { return maxSalary; }
    public void setMaxSalary(Double maxSalary) { this.maxSalary = maxSalary; }
    //public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    //public int getOpenPositions() { return openPositions; }
    public void setOpenPositions(int openPositions) { this.openPositions = openPositions; }
    //public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    //public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    //public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    //public Long getEmployerId() { return employerId; }
    public void setEmployerId(Long employerId) { this.employerId = employerId; }
    //public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    //public String getCompanyLocation() { return companyLocation; }
    public void setCompanyLocation(String companyLocation) { this.companyLocation = companyLocation; }
    //public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    //public int getTotalApplicants() { return totalApplicants; }
    public void setTotalApplicants(int totalApplicants) { this.totalApplicants = totalApplicants; }
}
