package OOP.JobPortal.ResumeMatchingSystem.Enums;

/**
 * UserRole - Defines the three possible roles a user can have in the system.
 */
public enum UserRole {

    /**
     * JOB_SEEKER: Can register, build a resume, browse jobs,
     * apply for jobs, and track application status.
     */
    JOB_SEEKER,

    /**
     * EMPLOYER: Can post job listings, view applicants for their jobs,
     * advance or reject candidates through the hiring pipeline.
     */
    EMPLOYER,

    /**
     * ADMIN: Has full platform access. Can view all users, deactivate
     * accounts, and view platform-wide analytics.
     * Admin accounts are created directly in the database — no public
     * registration endpoint exists for security reasons.
     */
    ADMIN
}