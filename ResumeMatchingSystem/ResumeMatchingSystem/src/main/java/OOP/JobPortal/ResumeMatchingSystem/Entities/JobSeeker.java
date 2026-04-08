package OOP.JobPortal.ResumeMatchingSystem.Entities;

import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;
import OOP.JobPortal.ResumeMatchingSystem.Enums.UserRole;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * JobSeeker  –  Concrete Subclass of User
 * ============================================================
 *
 * Inheritance:
 *   JobSeeker IS-A User. It inherits all fields and methods
 *   from User (id, email, password, role, getters/setters, etc.)
 *   and ADDS its own job-seeker-specific fields.
 *
 * Super classes and sub-classes:
 *   The 'extends User' keyword establishes this relationship.
 *   JobSeeker can access User's protected members directly.
 *
 * Constructor Chaining:
 *   JobSeeker's constructor calls super(...) to invoke the
 *   parent class constructor, ensuring all User fields are set.
 *
 * Method Overriding:
 *   getUserType() is declared abstract in User and overridden here.
 *   This is the foundation of runtime polymorphism.
 *
 * Composition (HAS-A relationship):
 *   JobSeeker HAS-A Resume.
 *   The Resume is owned by the JobSeeker — if the JobSeeker is
 *   deleted, the Resume is also deleted (CascadeType.ALL).
 *   This is stronger than association: the Resume cannot exist
 *   without its JobSeeker.
 *
 * Object Relationships:
 *   JobSeeker → Resume:       Composition (strong ownership)
 *   JobSeeker → Application:  Association (both can exist independently)
 * ============================================================
 */
@Entity
@Table(name = "job_seekers")
public class JobSeeker extends User {

    // ================================================================
    //  Encapsulation: all fields are private
    // ================================================================

    /**
     * The city where this seeker wants to work.
     * Used by the job search filter: GET /api/jobs?location=Lahore
     */
    @Column(name = "preferred_location")
    private String preferredLocation;

    /** The type of work shift this seeker prefers */
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_shift")
    private ShiftType preferredShift;

    /**
     * A brief professional summary or headline.
     * columnDefinition = "TEXT" uses MySQL's TEXT type (up to 65,535 chars)
     * instead of VARCHAR (up to 255 chars by default).
     */
    @Column(name = "profile_summary", columnDefinition = "TEXT")
    private String profileSummary;

    /** Total years of professional work experience */
    @Column(name = "total_experience_years")
    private int totalExperienceYears;

    // ================================================================
    //  Composition (HAS-A):
    //   A Resume belongs entirely to this JobSeeker.
    //   CascadeType.ALL: save/delete seeker → save/delete resume too.
    //   orphanRemoval = true: removing resume from seeker deletes it from DB.
    //   mappedBy = "jobSeeker": the Resume table holds the foreign key column.
    // ================================================================

    /**
     * The resume owned by this job seeker.
     * Modeled as Composition: Resume cannot exist without JobSeeker.
     */
    @OneToOne(mappedBy = "jobSeeker", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Resume resume;

    // ================================================================
    //  Association (HAS-MANY, weaker than composition):
    //   A JobSeeker has many Applications, but an Application also
    //   references the JobListing — it exists independently.
    // ================================================================

    /**
     * All job applications this seeker has submitted.
     * List is initialized as an empty ArrayList — never null.
     */
    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Application> applications = new ArrayList<>();

    // ================================================================
    //  Static Class Member:
    //   Tracks how many JobSeeker instances have been created.
    //   Shared across all instances — not per-object.
    // ================================================================

    /** Total JobSeeker instances created during this application session */
    private static int totalSeekersCreated = 0;

    // ================================================================
    //  Default No-Argument Constructor
    // Required by JPA. Protected access.
    // ================================================================

    /** No-argument constructor required by JPA/Hibernate */
    protected JobSeeker() {
        super();   // Calls User's no-argument constructor
    }

    // ================================================================
    // Constructor Chaining:
    //   super(fullName, email, password, UserRole.JOB_SEEKER)
    //   calls the parent User constructor, passing the fixed role.
    // ================================================================

    /**
     * Primary constructor for creating a new JobSeeker.
     * Chains to the parent User constructor using super().
     *
     * @param fullName full display name
     * @param email    login email (must be unique)
     * @param password BCrypt-hashed password
     */
    public JobSeeker(String fullName, String email, String password) {
        // Constructor chaining: call the parent (User) constructor
        // We pass UserRole.JOB_SEEKER because this is always a seeker
        super(fullName, email, password, UserRole.JOB_SEEKER);

        // Set default values for seeker-specific fields
        this.totalExperienceYears = 0;
        totalSeekersCreated++;

        //  this reference: refers to current JobSeeker instance
        System.out.println("[JobSeeker] Created: " + this.getEmail()
                + " | Total seekers: " + totalSeekersCreated);
    }

    /**
     *  Method Overloading: additional constructor with more parameters.
     * Same method name (constructor name) — different parameter list.
     *
     * @param fullName          full display name
     * @param email             login email
     * @param password          BCrypt-hashed password
     * @param preferredLocation city the seeker wants to work in
     * @param preferredShift    shift type preference
     * @param totalExperience   years of professional experience
     */
    public JobSeeker(String fullName, String email, String password,
                     String preferredLocation, ShiftType preferredShift,
                     int totalExperience) {
        // First, call the simpler constructor using this() — chaining within same class
        this(fullName, email, password);

        // Then set the additional fields
        this.preferredLocation    = preferredLocation;
        this.preferredShift       = preferredShift;
        this.totalExperienceYears = totalExperience;
    }

    // ================================================================
    //  Method Overriding:
    //   getUserType() is abstract in User, overridden here.
    //   @Override annotation tells the compiler we're intentionally
    //   overriding — it will error if the method doesn't exist in parent.
    // ================================================================

    /**
     * Overrides the abstract method from User.
     * Returns the specific type string for this user.
     * This enables runtime polymorphism (dynamic method dispatch):
     *   User u = new JobSeeker(...);
     *   u.getUserType(); // → "JobSeeker" (not User.getUserType)
     *
     * @return "JobSeeker"
     */
    @Override
    public String getUserType() {
        return "JobSeeker";
    }

    // ================================================================
    //  Getters and Setters (manually written, no Lombok)
    // ================================================================

    /** Returns the city where this seeker wants to work */
    public String getPreferredLocation() {
        return preferredLocation;
    }

    /** Sets the preferred job location */
    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    /** Returns the preferred shift type */
    public ShiftType getPreferredShift() {
        return preferredShift;
    }

    /** Sets the preferred shift type */
    public void setPreferredShift(ShiftType preferredShift) {
        this.preferredShift = preferredShift;
    }

    /** Returns the professional summary / headline */
    public String getProfileSummary() {
        return profileSummary;
    }

    /** Sets the profile summary */
    public void setProfileSummary(String profileSummary) {
        this.profileSummary = profileSummary;
    }

    /** Returns total years of work experience */
    public int getTotalExperienceYears() {
        return totalExperienceYears;
    }

    /**
     * Sets total experience years with validation.
     * Experience cannot be negative.
     *
     * @param totalExperienceYears years of experience (must be >= 0)
     * @throws IllegalArgumentException if negative
     */
    public void setTotalExperienceYears(int totalExperienceYears) {
        if (totalExperienceYears < 0) {
            throw new IllegalArgumentException(
                    "Experience years cannot be negative: " + totalExperienceYears);
        }
        this.totalExperienceYears = totalExperienceYears;
    }

    /** Returns the resume (may be null if not yet created) */
    public Resume getResume() {
        return resume;
    }

    /** Sets the resume (used internally by JPA and ResumeService) */
    public void setResume(Resume resume) {
        this.resume = resume;
    }

    /** Returns the list of all applications this seeker has submitted */
    public List<Application> getApplications() {
        return applications;
    }

    /** Sets the applications list (used by JPA) */
    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    // ================================================================
    // Static Member Getter
    // ================================================================

    /** Returns how many JobSeeker instances were created this session */
    public static int getTotalSeekersCreated() {
        return totalSeekersCreated;
    }

    // ================================================================
    // Business Logic Methods
    // ================================================================

    /**
     * Convenience method: checks whether this seeker already has a resume.
     * Returns true if a resume has been created and linked.
     */
    public boolean hasResume() {
        return this.resume != null;
    }

    /**
     * Returns the count of applications that have not been rejected.
     * Uses the Stream API to filter and count.
     */
    public int getActiveApplicationCount() {
        return (int) applications.stream()
                .filter(app -> app.getStage() != OOP.JobPortal.ResumeMatchingSystem.Enums.AppStage.REJECTED)
                .count();
    }

    /**
     * Returns a formatted display string for console output.
     * Arrays and Strings: demonstrates string manipulation.
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Job Seeker Profile ===\n");
        sb.append("Name:       ").append(getFullName()).append("\n");
        sb.append("Email:      ").append(getEmail()).append("\n");
        sb.append("Location:   ").append(preferredLocation != null ? preferredLocation : "Not set").append("\n");
        sb.append("Shift:      ").append(preferredShift != null ? preferredShift : "Not set").append("\n");
        sb.append("Experience: ").append(totalExperienceYears).append(" years\n");
        sb.append("Has Resume: ").append(hasResume() ? "Yes" : "No").append("\n");
        sb.append("Applications: ").append(applications.size()).append("\n");
        return sb.toString();
    }
}