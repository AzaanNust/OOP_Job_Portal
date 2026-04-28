package OOP.JobPortal.ResumeMatchingSystem.Entities;

import OOP.JobPortal.ResumeMatchingSystem.Enums.AppStage;
import OOP.JobPortal.ResumeMatchingSystem.Exceptions.InvalidStageTransitionException;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * ============================================================
 * Application  –  Association Bridge Entity
 * ============================================================
 *
 *  Association:
 *   Application is a classic Many-to-Many bridge class.
 *   One JobSeeker can apply to many jobs.
 *   One JobListing can receive applications from many seekers.
 *   The Application entity sits between them, carrying extra data
 *   (matchScore, stage, coverLetter) that neither side owns alone.
 *
 *  Exception Handling:
 *   advanceStage() uses try-catch-finally and throws a custom
 *   checked exception: InvalidStageTransitionException.
 *   The finally block always logs the attempt.
 *
 *  SOLID Principles:
 *   Single Responsibility: this class only manages the state of
 *   one application. It doesn't send notifications or calculate scores.
 *
 * The @UniqueConstraint prevents a seeker from applying to the
 * same job twice at the database level (additional protection beyond
 * the existsByJobSeekerIdAndJobListingId() check in the service).
 * ============================================================
 */
@Entity
@Table(
        name = "applications",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"job_seeker_id", "job_listing_id"},
                name        = "uk_seeker_job"   // unique: one application per seeker+job pair
        )
)
public class Application {

    // ── Private fields (Encapsulation) ────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── ASSOCIATION: links to JobSeeker ───────────────────────────

    /**
     * The job seeker who submitted this application.
     * ManyToOne: many applications can belong to one seeker.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    // ── ASSOCIATION: links to JobListing ──────────────────────────

    /**
     * The job listing this application is for.
     * ManyToOne: many applications can target the same job.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_listing_id", nullable = false)
    private JobListing jobListing;

    // ── Application Data ──────────────────────────────────────────

    /**
     * Match score from 0.0 to 100.0.
     * Calculated by SkillBasedMatcher when the application is submitted.
     * Higher score = better skills alignment with the job.
     */
    @Column(name = "match_score", nullable = false)
    private double matchScore = 0.0;

    /**
     * Comma-separated list of skills the seeker should learn for this job.
     * Calculated at apply-time alongside matchScore.
     * Example: "Docker, Kubernetes, Redis"
     */
    @Column(name = "skills_to_improve", columnDefinition = "TEXT")
    private String skillsToImprove;

    /** Current stage in the hiring pipeline */
    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private AppStage stage = AppStage.APPLIED;  // All applications start as APPLIED

    /** Optional cover letter written by the seeker */
    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    /** Private notes written by the employer about this applicant */
    @Column(name = "employer_notes", columnDefinition = "TEXT")
    private String employerNotes;

    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Constructors ───────────────────────────────────────────────

    /** Default constructor required by JPA */
    public Application() {
        this.stage     = AppStage.APPLIED;
        this.matchScore = 0.0;
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructor for creating a new application.
     *
     * @param jobSeeker  the applicant
     * @param jobListing the job being applied to
     * @param matchScore the calculated compatibility score (0–100)
     */
    public Application(JobSeeker jobSeeker, JobListing jobListing, double matchScore) {
        this();
        this.jobSeeker  = jobSeeker;
        this.jobListing = jobListing;
        this.matchScore = matchScore;
    }

    // ── Lifecycle hooks ────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters and Setters ────────────────────────────────────────

    /** Returns the application ID */
    public Long getId() {
        return id;
    }

    /** Returns the applicant */
    public JobSeeker getJobSeeker() {
        return jobSeeker;
    }

//    /** Sets the applicant */
//    public void setJobSeeker(JobSeeker jobSeeker) {
//        this.jobSeeker = jobSeeker;
//    }

    /** Returns the target job listing */
    public JobListing getJobListing() {
        return jobListing;
    }

//    /** Sets the target job listing */
//    public void setJobListing(JobListing jobListing) {
//        this.jobListing = jobListing;
//    }

    /** Returns the match score (0.0 – 100.0) */
    public double getMatchScore() {
        return matchScore;
    }

//    /** Sets the match score */
//    public void setMatchScore(double matchScore) {
//        this.matchScore = matchScore;
//    }

    /** Returns the skills-to-improve string */
    public String getSkillsToImprove() {
        return skillsToImprove;
    }

    /** Sets the skills-to-improve string */
    public void setSkillsToImprove(String skillsToImprove) {
        this.skillsToImprove = skillsToImprove;
    }

    /** Returns the current pipeline stage */
    public AppStage getStage() {
        return stage;
    }

//    /** Sets the pipeline stage directly (used by JPA) */
//    public void setStage(AppStage stage) {
//        this.stage = stage;
//    }

    /** Returns the cover letter text */
    public String getCoverLetter() {
        return coverLetter;
    }

    /** Sets the cover letter */
    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    /** Returns employer notes */
    public String getEmployerNotes() {
        return employerNotes;
    }

    /** Sets employer notes */
    public void setEmployerNotes(String employerNotes) {
        this.employerNotes = employerNotes;
    }

    /** Returns when this application was submitted */
    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

//    /** Returns when this application was last updated */
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }

    // ================================================================
    // Exception Handling: try-catch-finally
    // OOP Case Study: pipeline state machine
    // ================================================================

    /**
     * Advances this application to the next stage in the hiring pipeline.
     *
     * Pipeline order:
     *   APPLIED → SCREENING → INTERVIEW_SCHEDULED → OFFER_SENT → HIRED
     *
     * Custom Exception:
     *   Throws InvalidStageTransitionException (a checked exception)
     *   if the application is already in a terminal state.
     *
     * try-catch-finally:
     *   The finally block always executes — even if an exception is thrown.
     *   Used here for audit logging.
     *
     * @throws InvalidStageTransitionException if the application is HIRED or REJECTED
     */
    public void advanceStage() throws InvalidStageTransitionException {
        AppStage previousStage = this.stage;

        try {
            // Switch expression — modern Java 14+ feature
            AppStage nextStage = switch (this.stage) {
                case APPLIED              -> AppStage.SCREENING;
                case SCREENING            -> AppStage.INTERVIEW_SCHEDULED;
                case INTERVIEW_SCHEDULED  -> AppStage.OFFER_SENT;
                case OFFER_SENT           -> AppStage.HIRED;
                case HIRED, REJECTED      ->
                        throw new InvalidStageTransitionException(
                                "Cannot advance application #" + id
                                        + " — it is already in terminal stage: " + this.stage
                        );
            };

            this.stage     = nextStage;
            this.updatedAt = LocalDateTime.now();

        } catch (InvalidStageTransitionException e) {
            // Re-throw checked exceptions so the caller must handle them
            throw e;
        } finally {
            // finally block: always executes regardless of exceptions
            // This logs every stage transition attempt for auditing
            System.out.println("[Application] Stage transition attempt for #" + id
                    + " | From: " + previousStage + " | To: " + this.stage);
        }
    }

    /**
     * Marks this application as rejected.
     *
     *  Exception Handling:
     *   Throws InvalidStageTransitionException if already HIRED.
     *
     * @throws InvalidStageTransitionException if application is already HIRED
     */
    public void reject() throws InvalidStageTransitionException {
        try {
            if (this.stage == AppStage.HIRED) {
                throw new InvalidStageTransitionException(
                        "Cannot reject application #" + id + " — candidate is already HIRED");
            }
            this.stage     = AppStage.REJECTED;
            this.updatedAt = LocalDateTime.now();

        } finally {
            System.out.println("[Application] Reject called for #" + id
                    + " | Final stage: " + this.stage);
        }
    }

//    /** Returns true if this application is in a terminal state */
//    public boolean isTerminal() {
//        return this.stage == AppStage.HIRED || this.stage == AppStage.REJECTED;
//    }

    @Override
    public String toString() {
        return "Application{"
                + "id=" + id
                + ", stage=" + stage
                + ", matchScore=" + matchScore
                + "}";
    }
}