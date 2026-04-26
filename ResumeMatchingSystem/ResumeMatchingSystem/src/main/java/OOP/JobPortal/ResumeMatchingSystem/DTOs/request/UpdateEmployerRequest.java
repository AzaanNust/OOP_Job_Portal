package OOP.JobPortal.ResumeMatchingSystem.DTOs.request;

/** Payload for updating an employer's profile and company details */
public class UpdateEmployerRequest {
    private String fullName;
    private String phoneNumber;
    private String companyName;
    private String industry;
    private String companyLocation;
    private String companyWebsite;
    private String companyDescription;
    private String companySize;

    public UpdateEmployerRequest() {}

    public String getFullName()                       { return fullName; }
    public void   setFullName(String v)               { this.fullName = v; }
    public String getPhoneNumber()                    { return phoneNumber; }
    public void   setPhoneNumber(String v)            { this.phoneNumber = v; }
    public String getCompanyName()                    { return companyName; }
    public void   setCompanyName(String v)            { this.companyName = v; }
    public String getIndustry()                       { return industry; }
    public void   setIndustry(String v)               { this.industry = v; }
    public String getCompanyLocation()                { return companyLocation; }
    public void   setCompanyLocation(String v)        { this.companyLocation = v; }
    public String getCompanyWebsite()                 { return companyWebsite; }
    public void   setCompanyWebsite(String v)         { this.companyWebsite = v; }
    public String getCompanyDescription()             { return companyDescription; }
    public void   setCompanyDescription(String v)     { this.companyDescription = v; }
    public String getCompanySize()                    { return companySize; }
    public void   setCompanySize(String v)            { this.companySize = v; }
}