package OOP.JobPortal.ResumeMatchingSystem.DTOs.request;

import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;

/** Payload for updating a job seeker's profile */
public class UpdateSeekerRequest {
    private String fullName;
    private String phoneNumber;
    private String preferredLocation;
    private ShiftType preferredShift;
    private int totalExperienceYears;
    private String profileSummary;

    public UpdateSeekerRequest() {}

    public String getFullName()                          { return fullName; }
    public void   setFullName(String v)                  { this.fullName = v; }
    public String getPhoneNumber()                       { return phoneNumber; }
    public void   setPhoneNumber(String v)               { this.phoneNumber = v; }
    public String getPreferredLocation()                 { return preferredLocation; }
    public void   setPreferredLocation(String v)         { this.preferredLocation = v; }
    public ShiftType getPreferredShift()                 { return preferredShift; }
    public void   setPreferredShift(ShiftType v)         { this.preferredShift = v; }
    public int    getTotalExperienceYears()              { return totalExperienceYears; }
    public void   setTotalExperienceYears(int v)         { this.totalExperienceYears = v; }
    public String getProfileSummary()                    { return profileSummary; }
    public void   setProfileSummary(String v)            { this.profileSummary = v; }
}