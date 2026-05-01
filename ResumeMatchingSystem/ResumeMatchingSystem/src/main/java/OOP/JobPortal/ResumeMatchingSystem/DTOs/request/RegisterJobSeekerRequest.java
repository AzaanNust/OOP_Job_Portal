package OOP.JobPortal.ResumeMatchingSystem.DTOs.request;
import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.*;
/** Registration payload for job seekers */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RegisterJobSeekerRequest {
    @NotBlank(message = "Full name is required") private String fullName;
    @Email @NotBlank(message = "Email is required") private String email;
    @NotBlank @Size(min=6, message="Password must be at least 6 characters") private String password;
    private String phoneNumber;
    private String preferredLocation;
    private ShiftType preferredShift;
    private int totalExperienceYears;
    private String profileSummary;
    public RegisterJobSeekerRequest() {}
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhoneNumber() { return phoneNumber; }
    //public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPreferredLocation() { return preferredLocation; }
    //public void setPreferredLocation(String preferredLocation) { this.preferredLocation = preferredLocation; }
    public ShiftType getPreferredShift() { return preferredShift; }
    //public void setPreferredShift(ShiftType preferredShift) { this.preferredShift = preferredShift; }
    public int getTotalExperienceYears() { return totalExperienceYears; }
    //public void setTotalExperienceYears(int totalExperienceYears) { this.totalExperienceYears = totalExperienceYears; }
    public String getProfileSummary() { return profileSummary; }
    //public void setProfileSummary(String profileSummary) { this.profileSummary = profileSummary; }
}
