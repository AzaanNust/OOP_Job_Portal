package OOP.JobPortal.ResumeMatchingSystem.Enums;

/**
 * JobStatus - Whether a job listing is currently accepting applications.
 * When an employer closes a job, its status changes to CLOSED.
 * The ApplicationService checks this before allowing a seeker to apply:
 * if status != OPEN, a BusinessException is thrown.
 */
public enum JobStatus {
    OPEN,    // Accepting new applications
    CLOSED,  // No longer accepting applications
    PAUSED   // Temporarily paused by employer
}