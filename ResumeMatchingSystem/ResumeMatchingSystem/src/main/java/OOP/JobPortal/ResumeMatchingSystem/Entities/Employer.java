package OOP.JobPortal.ResumeMatchingSystem.Entities;

import OOP.JobPortal.ResumeMatchingSystem.Enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * Employer  –  Concrete Subclass of User
 * ============================================================
 *
 *  Inheritance (IS-A):
 *   Employer IS-A User. Inherits all User fields.
 *   Demonstrates that one abstract parent can have multiple children:
 *   User → JobSeeker, User → Employer, User → Admin.
 *
 *  Composition (HAS-A):
 *   Employer HAS-MANY JobListings.
 *   The job listings are owned by the employer. If the employer
 *   account is deleted, all their job listings are deleted too.
 *
 *  Single Responsibility Principle (SRP):
 *   Employer only handles employer-specific data.
 *   Authentication, validation, and business logic are in separate classes.
 * ============================================================
 */
@Entity
@Table(name = "employers")
public class Employer extends User {

    // ── Private fields (Encapsulation) ────────────────────────────

    /** The name of the company this employer represents */
    @NotBlank(message = "Company name is required")
    @Column(name = "company_name", nullable = false)
    private String companyName;

    /** Optional company website URL */
    @Column(name = "company_website")
    private String companyWebsite;

    /** Industry sector (e.g., "Information Technology", "Healthcare") */
    @Column(name = "industry")
    private String industry;

    /** City where the company is headquartered */
    @Column(name = "company_location")
    private String companyLocation;

    /** Description of the company — shown on job listings */
    @Column(name = "company_description", columnDefinition = "TEXT")
    private String companyDescription;

    /**
     * Company size range (e.g., "1-10", "11-50", "51-200", "200+").
     * Stored as a string to allow flexible range descriptions.
     */
    @Column(name = "company_size")
    private String companySize;

    // ── COMPOSITION: Employer owns JobListings ─────────────────────

    /**
     * All job listings posted by this employer.
     * Cascade ALL: saving/deleting employer also saves/deletes its jobs.
     */
    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JobListing> jobListings = new ArrayList<>();

    // ── Static counter ─────────────────────────────────────────────

    /** Counts how many Employer instances have been created */
    private static int totalEmployersCreated = 0;

    // ── Constructors ───────────────────────────────────────────────

    /**
     * Default no-argument constructor required by JPA.
     */
    protected Employer() {
        super();
    }

    /**
     * Primary constructor.
     * Constructor Chaining: calls super() to set User fields.
     *
     * @param fullName    contact person's full name
     * @param email       login email
     * @param password    BCrypt-hashed password
     * @param companyName name of the company
     */
    public Employer(String fullName, String email, String password, String companyName) {
        super(fullName, email, password, UserRole.EMPLOYER);
        this.companyName = companyName;
        totalEmployersCreated++;
        System.out.println("[Employer] Created: " + companyName
                + " | Total employers: " + totalEmployersCreated);
    }

    /**
     *  Method Overloading: full constructor with all optional fields.
     *
     * @param fullName          contact name
     * @param email             login email
     * @param password          hashed password
     * @param companyName       company name (required)
     * @param industry          industry sector
     * @param companyLocation   company city
     * @param companyDescription company description
     */
    public Employer(String fullName, String email, String password,
                    String companyName, String industry,
                    String companyLocation, String companyDescription) {
        this(fullName, email, password, companyName);  // delegate to simpler constructor
        this.industry           = industry;
        this.companyLocation    = companyLocation;
        this.companyDescription = companyDescription;
    }

    // ── Method Overriding ──────────────────────────────────────────

    /**
     * Method Overriding:
     * Returns "Employer" to identify this subclass at runtime.
     */
    @Override
    public String getUserType() {
        return "Employer";
    }

    // ── Getters and Setters ────────────────────────────────────────

    /** Returns the company name */
    public String getCompanyName() {
        return companyName;
    }

    /** Sets the company name */
    public void setCompanyName(String companyName) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be blank");
        }
        this.companyName = companyName;
    }

    /** Returns the company website URL */
    public String getCompanyWebsite() {
        return companyWebsite;
    }

    /** Sets the company website URL */
    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    /** Returns the industry sector */
    public String getIndustry() {
        return industry;
    }

    /** Sets the industry sector */
    public void setIndustry(String industry) {
        this.industry = industry;
    }

    /** Returns the company's city */
    public String getCompanyLocation() {
        return companyLocation;
    }

    /** Sets the company's city */
    public void setCompanyLocation(String companyLocation) {
        this.companyLocation = companyLocation;
    }

    /** Returns the company description */
    public String getCompanyDescription() {
        return companyDescription;
    }

    /** Sets the company description */
    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    /** Returns the company size range string */
    public String getCompanySize() {
        return companySize;
    }

    /** Sets the company size range */
    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    /** Returns all job listings posted by this employer */
    public List<JobListing> getJobListings() {
        return jobListings;
    }

    /** Sets the job listings list (used by JPA) */
    public void setJobListings(List<JobListing> jobListings) {
        this.jobListings = jobListings;
    }

    /** Returns how many Employer instances were created this session */
    public static int getTotalEmployersCreated() {
        return totalEmployersCreated;
    }

    /** Returns how many job listings this employer has posted */
    public int getTotalJobsPosted() {
        return jobListings.size();
    }

    /** Returns count of open job listings */
    public long getOpenJobCount() {
        return jobListings.stream()
                .filter(j -> j.getStatus() == OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus.OPEN)
                .count();
    }
}