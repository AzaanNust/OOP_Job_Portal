package OOP.JobPortal.ResumeMatchingSystem.Entities;

import OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus;
import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * JobListing  –  Composition with Employer
 * ============================================================
 *
 * Composition:
 *   A JobListing is owned by an Employer.
 *   It cannot exist without an Employer.
 *
 * UML Relationships:
 *   Employer ◆—— JobListing   (composition: employer owns listings)
 *   JobListing ——— Application (association: both can exist independently)
 *
 * Encapsulation:
 *   requiredSkills is stored as a comma-separated string internally,
 *   but exposed as a List through getRequiredSkillsAsList().
 *   The consumer of this class does not know how skills are stored.
 *
 * SOLID / Open-Closed Principle (OCP):
 *   This entity is closed for modification (don't change existing fields)
 *   but open for extension (new fields can be added without breaking callers).
 * ============================================================
 */
@Entity
@Table(name = "job_listings")
public class JobListing {

    // ── Private fields (Encapsulation) ────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── COMPOSITION: belongs to exactly one Employer ───────────────

    /**
     * The employer who posted this job.
     * Many jobs can belong to one employer (Many-to-One).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    // ── Job Details ────────────────────────────────────────────────

    /** Job title, e.g., "Senior Java Developer" */
    @NotBlank(message = "Job title is required")
    @Column(nullable = false)
    private String title;

    /** Full description of the role and responsibilities */
    @NotBlank(message = "Job description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    /** City where the job is located (e.g., "Lahore", "Remote") */
    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    /** Type of work shift this job requires */
    @NotNull(message = "Shift type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type", nullable = false)
    private ShiftType shiftType;

    /** Current status of this job listing */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.OPEN;  // New listings start as OPEN

    /**
     * Comma-separated required skills.
     * Used by the MatchingStrategy to calculate match scores.
     */
    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    /**
     * Comma-separated preferred (nice-to-have) skills.
     * These contribute to the match score but are not mandatory.
     */
    @Column(name = "preferred_skills", columnDefinition = "TEXT")
    private String preferredSkills;

    /** Minimum years of experience required */
    @Column(name = "min_experience_years")
    private int minExperienceYears = 0;

    /** Maximum experience ceiling (can be null = no upper limit) */
    @Column(name = "max_experience_years")
    private Integer maxExperienceYears;

    /** Minimum monthly salary in PKR */
    @Column(name = "min_salary")
    private Double minSalary;

    /** Maximum monthly salary in PKR */
    @Column(name = "max_salary")
    private Double maxSalary;

    /** Employment type: "Full-time", "Part-time", "Contract", "Internship" */
    @Column(name = "job_type")
    private String jobType;

    /** Number of open positions */
    @Column(name = "open_positions")
    private int openPositions = 1;

    /** Application deadline date */
    @Column(name = "deadline")
    private LocalDateTime deadline;

    // ── Applications (Association, weaker than composition) ────────

    /**
     * All applications submitted for this job.
     * Association: applications can theoretically exist without this specific listing.
     */
    @OneToMany(mappedBy = "jobListing", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Application> applications = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Constructors ───────────────────────────────────────────────

    /** Default constructor required by JPA */
    public JobListing() {
        this.status        = JobStatus.OPEN;
        this.openPositions = 1;
        this.createdAt     = LocalDateTime.now();
        this.updatedAt     = LocalDateTime.now();
    }

    /**
     * Constructor for creating a basic job listing.
     *
     * @param employer    the employer posting this job
     * @param title       job title
     * @param description job description
     * @param location    city where the job is based
     * @param shiftType   required shift type
     */
    public JobListing(Employer employer, String title, String description,
                      String location, ShiftType shiftType) {
        this();  // Call default constructor
        this.employer    = employer;
        this.title       = title;
        this.description = description;
        this.location    = location;
        this.shiftType   = shiftType;
    }

    // ── Lifecycle hooks ────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters and Setters ────────────────────────────────────────

    /** Returns the job listing ID */
    public Long getId() {
        return id;
    }

    /** Returns the posting employer */
    public Employer getEmployer() {
        return employer;
    }

    /** Sets the posting employer */
    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    /** Returns the job title */
    public String getTitle() {
        return title;
    }

    /** Sets the job title */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Returns the job description */
    public String getDescription() {
        return description;
    }

    /** Sets the job description */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Returns the job location / city */
    public String getLocation() {
        return location;
    }

    /** Sets the job location */
    public void setLocation(String location) {
        this.location = location;
    }

    /** Returns the required shift type */
    public ShiftType getShiftType() {
        return shiftType;
    }

    /** Sets the shift type */
    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    /** Returns the current job status */
    public JobStatus getStatus() {
        return status;
    }

    /** Sets the job status */
    public void setStatus(JobStatus status) {
        this.status = status;
    }

    /** Returns the raw required skills string */
    public String getRequiredSkills() {
        return requiredSkills;
    }

    /** Sets the required skills string */
    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    /** Returns the raw preferred skills string */
    public String getPreferredSkills() {
        return preferredSkills;
    }

    /** Sets the preferred skills string */
    public void setPreferredSkills(String preferredSkills) {
        this.preferredSkills = preferredSkills;
    }

    /** Returns minimum years of experience */
    public int getMinExperienceYears() {
        return minExperienceYears;
    }

    /** Sets minimum experience years */
    public void setMinExperienceYears(int minExperienceYears) {
        this.minExperienceYears = minExperienceYears;
    }

    /** Returns maximum experience years (null = no limit) */
    public Integer getMaxExperienceYears() {
        return maxExperienceYears;
    }

    /** Sets maximum experience years */
    public void setMaxExperienceYears(Integer maxExperienceYears) {
        this.maxExperienceYears = maxExperienceYears;
    }

    /** Returns minimum salary */
    public Double getMinSalary() {
        return minSalary;
    }

    /** Sets minimum salary */
    public void setMinSalary(Double minSalary) {
        this.minSalary = minSalary;
    }

    /** Returns maximum salary */
    public Double getMaxSalary() {
        return maxSalary;
    }

    /** Sets maximum salary */
    public void setMaxSalary(Double maxSalary) {
        this.maxSalary = maxSalary;
    }

    /** Returns the job type string */
    public String getJobType() {
        return jobType;
    }

    /** Sets the job type */
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    /** Returns number of open positions */
    public int getOpenPositions() {
        return openPositions;
    }

    /** Sets number of open positions */
    public void setOpenPositions(int openPositions) {
        this.openPositions = openPositions;
    }

    /** Returns the application deadline */
    public LocalDateTime getDeadline() {
        return deadline;
    }

    /** Sets the application deadline */
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    /** Returns all applications for this job */
    public List<Application> getApplications() {
        return applications;
    }

    /** Sets the applications list */
    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    /** Returns when this listing was created */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Returns when this listing was last updated */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ── Business methods ───────────────────────────────────────────

    /**
     * Converts the comma-separated required skills string to a clean List.
     *  String/Array manipulation.
     *  Encapsulation: hides internal storage format.
     *
     * @return list of required skills (lowercase, trimmed)
     */
    public List<String> getRequiredSkillsAsList() {
        if (this.requiredSkills == null || this.requiredSkills.isBlank()) {
            return new ArrayList<>();
        }
        String[] parts = this.requiredSkills.split(",");
        List<String> result = new ArrayList<>();
        for (String skill : parts) {
            String trimmed = skill.trim().toLowerCase();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * Converts the preferred skills string to a List.
     * Same pattern as getRequiredSkillsAsList().
     *
     * @return list of preferred skills (lowercase, trimmed)
     */
    public List<String> getPreferredSkillsAsList() {
        if (this.preferredSkills == null || this.preferredSkills.isBlank()) {
            return new ArrayList<>();
        }
        String[] parts = this.preferredSkills.split(",");
        List<String> result = new ArrayList<>();
        for (String skill : parts) {
            String trimmed = skill.trim().toLowerCase();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * Closes this job listing — no more applications will be accepted.
     * Business logic: enforces the state transition.
     */
    public void closeJob() {
        this.status    = JobStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
        System.out.println("[JobListing] Job closed: " + this.title);
    }

    /** Returns whether this job is currently accepting applications */
    public boolean isAcceptingApplications() {
        return this.status == JobStatus.OPEN;
    }

    @Override
    public String toString() {
        return "JobListing{"
                + "id=" + id
                + ", title='" + title + "'"
                + ", location='" + location + "'"
                + ", shift=" + shiftType
                + ", status=" + status
                + ", applicants=" + applications.size()
                + "}";
    }
}