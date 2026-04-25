package OOP.JobPortal.ResumeMatchingSystem.Enums;

/**
 * ShiftType - Defines the type of work shift for a job.
 * Used in two places:
 *   1. JobSeeker.preferredShift  → what shift the candidate wants
 *   2. JobListing.shiftType      → what shift the job requires
 * The job search API accepts shift as a filter parameter:
 *   GET /api/jobs?location=Lahore&shift=NIGHT
 */
public enum ShiftType {
    MORNING,     // Typically 6 AM – 2 PM
    AFTERNOON,   // Typically 2 PM – 10 PM
    NIGHT,       // Typically 10 PM – 6 AM (night shift)
    FLEXIBLE,    // No fixed hours (remote/hybrid friendly)
    FULL_TIME,   // Standard 9-to-5, five days a week
    PART_TIME    // Reduced hours per week
}
