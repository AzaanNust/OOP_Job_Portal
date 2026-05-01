package OOP.JobPortal.ResumeMatchingSystem.DTOs.request;
import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
/** Payload for posting or updating a job listing */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class JobListingRequest {
    @NotBlank(message = "Title is required") private String title;
    @NotBlank(message = "Description is required") private String description;
    @NotBlank(message = "Location is required") private String location;
    @NotNull(message = "Shift type is required") private ShiftType shiftType;
    private String requiredSkills;
    private String preferredSkills;
    private int minExperienceYears;
    private Integer maxExperienceYears;
    private Double minSalary;
    private Double maxSalary;
    private String jobType;
    private int openPositions = 1;
    private String deadline;
    public JobListingRequest() {}
    public String getTitle() { return title; }
    //public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    //public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    //public void setLocation(String location) { this.location = location; }
    public ShiftType getShiftType() { return shiftType; }
    //public void setShiftType(ShiftType shiftType) { this.shiftType = shiftType; }
    public String getRequiredSkills() { return requiredSkills; }
    //public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }
    public String getPreferredSkills() { return preferredSkills; }
    //public void setPreferredSkills(String preferredSkills) { this.preferredSkills = preferredSkills; }
    public int getMinExperienceYears() { return minExperienceYears; }
    //public void setMinExperienceYears(int minExperienceYears) { this.minExperienceYears = minExperienceYears; }
    public Integer getMaxExperienceYears() { return maxExperienceYears; }
    //public void setMaxExperienceYears(Integer maxExperienceYears) { this.maxExperienceYears = maxExperienceYears; }
    public Double getMinSalary() { return minSalary; }
    //public void setMinSalary(Double minSalary) { this.minSalary = minSalary; }
    public Double getMaxSalary() { return maxSalary; }
    //public void setMaxSalary(Double maxSalary) { this.maxSalary = maxSalary; }
    public String getJobType() { return jobType; }
    //public void setJobType(String jobType) { this.jobType = jobType; }
    public int getOpenPositions() { return openPositions; }
    //public void setOpenPositions(int openPositions) { this.openPositions = openPositions; }
    public String getDeadline() { return deadline; }
    //public void setDeadline(String deadline) { this.deadline = deadline; }
}
