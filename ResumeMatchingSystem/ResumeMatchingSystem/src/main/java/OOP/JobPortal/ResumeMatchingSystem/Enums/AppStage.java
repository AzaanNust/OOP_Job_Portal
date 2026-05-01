package OOP.JobPortal.ResumeMatchingSystem.Enums;

/**
 * AppStage - Represents the stages in the hiring pipeline.
 * A job application moves through these stages in order:
 * APPLIED → SCREENING → INTERVIEW_SCHEDULED → OFFER_SENT → HIRED
 * At any point it can also move to REJECTED (terminal state).
 */
public enum AppStage {

    /** Application just submitted — waiting for employer review */
    APPLIED,

    /** Employer is reviewing the resume */
    SCREENING,

    /** Candidate shortlisted — interview has been arranged */
    INTERVIEW_SCHEDULED,

    /** Interview passed — job offer has been sent */
    OFFER_SENT,

    /** Candidate accepted the offer — hiring complete (terminal) */
    HIRED,

    /** Application was not successful (terminal) */
    REJECTED
}