package oop.project.androidoopproject.model

// ── Enums ─────────────────────────────────────────────────────────

enum class UserRole { JOB_SEEKER, EMPLOYER, ADMIN }

enum class AppStage {
    APPLIED, SCREENING, INTERVIEW_SCHEDULED, OFFER_SENT, HIRED, REJECTED;
    fun displayName() = when(this) {
        APPLIED              -> "Applied"
        SCREENING            -> "Screening"
        INTERVIEW_SCHEDULED  -> "Interview"
        OFFER_SENT           -> "Offer Sent"
        HIRED                -> "Hired"
        REJECTED             -> "Rejected"
    }
    fun label() = displayName()
}

enum class ShiftType {
    MORNING, AFTERNOON, NIGHT, FLEXIBLE, FULL_TIME, PART_TIME;
    fun displayName() = when(this) {
        MORNING   -> "Morning";   AFTERNOON -> "Afternoon"; NIGHT     -> "Night"
        FLEXIBLE  -> "Flexible";  FULL_TIME -> "Full-Time"; PART_TIME -> "Part-Time"
    }
    fun label() = displayName()
    //It is equivalent to:
    //
    //fun label(): String {
    //    return displayName()
    //}
}

// ── Auth ──────────────────────────────────────────────────────────

data class LoginRequest(val email: String, val password: String)

// No exp years / preferred city / shift at registration —
// those are set in Profile and Resume after registering
data class RegisterSeekerRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val phoneNumber: String? = null
)

// No companyLocation — a company can exist in multiple cities (set per job)
data class RegisterEmployerRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val companyName: String,
    val phoneNumber: String? = null,
    val industry: String? = null,
    val companyDescription: String? = null
)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val fullName: String,
    val email: String,
    val role: UserRole
)

// ── Profile ───────────────────────────────────────────────────────

data class SeekerProfile(
    val id: Long? = null,
    val fullName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val preferredLocation: String? = null,   // used as a job-search filter, not on resume
    val preferredShift: ShiftType? = null,   // used as a job-search filter, not on resume
    val totalExperienceYears: Int? = 0,
    val profileSummary: String? = null,
    val role: UserRole? = null
)

data class EmployerProfile(
    val id: Long? = null,
    val fullName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val companyName: String? = null,
    val industry: String? = null,
    val companyWebsite: String? = null,
    val companyDescription: String? = null,
    val companySize: String? = null,
    val role: UserRole? = null
)

data class UpdateSeekerRequest(
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val preferredLocation: String? = null,
    val preferredShift: ShiftType? = null,
    val totalExperienceYears: Int? = null,
    val profileSummary: String? = null
)

data class UpdateEmployerRequest(
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val companyName: String? = null,
    val industry: String? = null,
    val companyWebsite: String? = null,
    val companyDescription: String? = null,
    val companySize: String? = null
    // No companyLocation — city is set per job listing
)

// ── Jobs ──────────────────────────────────────────────────────────

data class PostJobRequest(
    val title: String,
    val description: String,
    val location: String,
    val shiftType: ShiftType,
    val requiredSkills: String? = null,
    val preferredSkills: String? = null,
    val minExperienceYears: Int = 0,
    val jobType: String? = null,
    val minSalary: Double? = null,
    val maxSalary: Double? = null,
    val openPositions: Int = 1,
    val deadline: String? = null
)

data class JobListingResponse(
    val id: Long,
    val title: String? = null,
    val description: String? = null,
    val location: String? = null,
    val shiftType: ShiftType? = null,
    val requiredSkills: String? = null,
    val preferredSkills: String? = null,
    val minExperienceYears: Int? = 0,
    val minSalary: Double? = null,
    val maxSalary: Double? = null,
    val jobType: String? = null,
    val openPositions: Int? = 1,
    val status: String? = null,
    val createdAt: String? = null,
    val deadline: String? = null,
    val employerId: Long? = null,
    val companyName: String? = null,
    val companyLocation: String? = null,
    val industry: String? = null,
    val totalApplicants: Int? = 0
) {
    fun companyInitial()      = companyName?.firstOrNull()?.uppercaseChar()?.toString() ?: "C"
    fun requiredSkillsList()  = requiredSkills?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    fun preferredSkillsList() = preferredSkills?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    fun isOpen()              = status == "OPEN"
    fun salaryText() = when {
        minSalary != null && maxSalary != null -> "PKR ${minSalary.toLong()} – ${maxSalary.toLong()}"
        minSalary != null                      -> "PKR ${minSalary.toLong()}+"
        maxSalary != null                      -> "Up to PKR ${maxSalary.toLong()}"
        else                                   -> "Salary not specified"
    }
}

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val number: Int = 0,
    val size: Int = 10
)

// ── Resume ────────────────────────────────────────────────────────

data class SaveResumeRequest(
    val skills: String? = null,
    val workExperience: String? = null,
    val education: String? = null,
    val certifications: String? = null,
    val languages: String? = null,
    val projects: String? = null,
    val awards: String? = null,
    val volunteerWork: String? = null,
    val publications: String? = null,
    val referencesText: String? = null,
    val totalExperienceYears: Int? = null,
    val portfolioUrl: String? = null,
    val linkedinUrl: String? = null,
    val githubUrl: String? = null
)

data class ResumeResponse(
    val id: Long? = null,
    val jobSeekerId: Long? = null,
    val seekerName: String? = null,
    val skills: String? = null,
    val workExperience: String? = null,
    val education: String? = null,
    val certifications: String? = null,
    val languages: String? = null,
    val projects: String? = null,
    val awards: String? = null,
    val volunteerWork: String? = null,
    val publications: String? = null,
    val referencesText: String? = null,
    val totalExperienceYears: Int? = null,
    val portfolioUrl: String? = null,
    val linkedinUrl: String? = null,
    val githubUrl: String? = null,
    val aiGeneratedHtml: String? = null,
    val lastAiGeneratedAt: String? = null
) {
    fun hasAiResume() = !aiGeneratedHtml.isNullOrBlank()
    fun skillsList()  = skills?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
}

// ── Applications ──────────────────────────────────────────────────

data class ApplyRequest(val jobListingId: Long, val coverLetter: String = "")

data class ApplicationResponse(
    val id: Long,
    val jobListingId: Long? = null,
    val jobTitle: String? = null,
    val companyName: String? = null,
    val jobLocation: String? = null,
    val matchScore: Double? = 0.0,
    val skillsToImprove: List<String>? = null,
    val stage: AppStage? = AppStage.APPLIED,
    val coverLetter: String? = null,
    val employerNotes: String? = null,
    val appliedAt: String? = null,
    val seekerId: Long? = null,
    val seekerName: String? = null,
    val seekerEmail: String? = null
) {
    fun matchPercent()     = matchScore?.toInt() ?: 0
    fun requiredMissing()  = skillsToImprove?.filter { !it.contains("(preferred)") } ?: emptyList()
    fun preferredMissing() = skillsToImprove?.filter { it.contains("(preferred)") } ?: emptyList()
}

// ── Notifications ─────────────────────────────────────────────────

data class NotificationItem(
    val id: Long,
    val recipient: String? = null,
    val subject: String? = null,
    val message: String? = null,
    val isRead: Boolean = false,
    val createdAt: String? = null
)

data class NotificationCount(val unreadCount: Long)