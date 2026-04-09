package OOP.JobPortal.ResumeMatchingSystem.Entities;

import jakarta.persistence.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ============================================================
 * Resume  –  Composition with JobSeeker
 * ============================================================
 *
 *  Composition (HAS-A, strong ownership):
 *   A Resume CANNOT exist without a JobSeeker.
 *   This is the strongest form of "has-a" relationship.
 *   When a JobSeeker is deleted, their Resume is also deleted.
 *
 *  UML Object Relationships:
 *   JobSeeker ◆—————— Resume
 *   (filled diamond = composition)
 *
 * File Handling & Object Serialization:
 *   Resume implements Serializable so it can be serialized to a file.
 *   Methods saveToFile() and loadFromFile() demonstrate Week 14 concepts.
 *
 * Encapsulation:
 *   All fields private, all accessed through getters/setters.
 *   getSkillsAsList() is a business method that encapsulates the
 *   internal comma-string storage and exposes a clean List interface.
 * ============================================================
 */
@Entity
@Table(name = "resumes")
public class Resume implements Serializable {

    // ================================================================
    //  Object Serialization:
    //   serialVersionUID is required for Serializable classes.
    //   It ensures the serialized form is compatible across code versions.
    // ================================================================

    /** Required for Java serialization compatibility */
    private static final long serialVersionUID = 1L;

    // ── Private fields (Encapsulation) ────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── COMPOSITION: belongs to exactly one JobSeeker ──────────────

    /**
     * The owner of this resume.
     * @JoinColumn creates a foreign key column 'job_seeker_id' in this table.
     * nullable = false enforces the composition rule: no orphan resumes.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    // ── Resume Content ─────────────────────────────────────────────

    /**
     * Skills stored as a comma-separated string.
     * Example: "Java, Spring Boot, MySQL, Docker, React"
     * Why comma-separated? Simpler to store and update vs a join table.
     * getSkillsAsList() converts this to a clean Java List.
     */
    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    /** Work history — free-form text describing past jobs */
    @Column(name = "work_experience", columnDefinition = "TEXT")
    private String workExperience;

    /** Educational background */
    @Column(name = "education", columnDefinition = "TEXT")
    private String education;

    /** Professional certifications (e.g., "Oracle Java SE 11, AWS Cloud") */
    @Column(name = "certifications", columnDefinition = "TEXT")
    private String certifications;

    /** Languages spoken (e.g., "English, Urdu") */
    @Column(name = "languages")
    private String languages;

    /** Portfolio website URL */
    @Column(name = "portfolio_url")
    private String portfolioUrl;

    /** LinkedIn profile URL */
    @Column(name = "linkedin_url")
    private String linkedinUrl;

    /** GitHub profile URL */
    @Column(name = "github_url")
    private String githubUrl;

    /**
     * AI-generated professional resume as HTML.
     * LONGTEXT can store up to 4GB of text — needed for full HTML documents.
     * This is converted to PDF when the download endpoint is called.
     */
    @Column(name = "ai_generated_html", columnDefinition = "LONGTEXT")
    private String aiGeneratedHtml;

    /**
     * Plain text version of the AI resume (HTML tags stripped).
     * Used by the matching algorithm for keyword analysis.
     */
    @Column(name = "ai_generated_text", columnDefinition = "LONGTEXT")
    private String aiGeneratedText;

    /** When the AI last generated this resume */
    @Column(name = "last_ai_generated_at")
    private LocalDateTime lastAiGeneratedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Constructors ───────────────────────────────────────────────

    /**
     * Default constructor required by JPA.
     */
    public Resume() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructor for creating a resume with the owning seeker.
     * WEEK 10 – Composition: the jobSeeker is required at construction.
     *
     * @param jobSeeker the owner of this resume (cannot be null)
     * @throws IllegalArgumentException if jobSeeker is null
     */
    public Resume(JobSeeker jobSeeker) {
        this();  // Call default constructor for timestamps
        if (jobSeeker == null) {
            throw new IllegalArgumentException("Resume must belong to a JobSeeker");
        }
        this.jobSeeker = jobSeeker;
    }

    /**
     * WEEK 4 – Copy Constructor:
     * Creates a copy of an existing resume with a new owner.
     * Useful for creating a resume template from another seeker's data.
     *
     * @param other     the resume to copy from
     * @param newOwner  the new JobSeeker who will own this copy
     */
    public Resume(Resume other, JobSeeker newOwner) {
        this(newOwner);
        this.skills        = other.skills;
        this.workExperience = other.workExperience;
        this.education     = other.education;
        this.certifications = other.certifications;
        this.languages     = other.languages;
        // Note: AI-generated content is NOT copied — must be regenerated
        // Note: portfolio/linkedin/github are NOT copied — personal to owner
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

    /** Returns the resume's database ID */
    public Long getId() {
        return id;
    }

    /** Returns the owning JobSeeker */
    public JobSeeker getJobSeeker() {
        return jobSeeker;
    }

    /** Sets the owning JobSeeker */
    public void setJobSeeker(JobSeeker jobSeeker) {
        this.jobSeeker = jobSeeker;
    }

    /** Returns the raw skills string (comma-separated) */
    public String getSkills() {
        return skills;
    }

    /**
     * Sets the skills string.
     * @param skills comma-separated skill names
     */
    public void setSkills(String skills) {
        this.skills    = skills;
        this.updatedAt = LocalDateTime.now();
    }

    /** Returns the work experience text */
    public String getWorkExperience() {
        return workExperience;
    }

    /** Sets the work experience text */
    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
        this.updatedAt      = LocalDateTime.now();
    }

    /** Returns the education text */
    public String getEducation() {
        return education;
    }

    /** Sets the education text */
    public void setEducation(String education) {
        this.education = education;
        this.updatedAt = LocalDateTime.now();
    }

    /** Returns the certifications string */
    public String getCertifications() {
        return certifications;
    }

    /** Sets the certifications */
    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    /** Returns the languages string */
    public String getLanguages() {
        return languages;
    }

    /** Sets the languages */
    public void setLanguages(String languages) {
        this.languages = languages;
    }

    /** Returns the portfolio URL */
    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    /** Sets the portfolio URL */
    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    /** Returns the LinkedIn URL */
    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    /** Sets the LinkedIn URL */
    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    /** Returns the GitHub URL */
    public String getGithubUrl() {
        return githubUrl;
    }

    /** Sets the GitHub URL */
    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    /** Returns the AI-generated HTML resume */
    public String getAiGeneratedHtml() {
        return aiGeneratedHtml;
    }

    /** Sets the AI-generated HTML resume */
    public void setAiGeneratedHtml(String aiGeneratedHtml) {
        this.aiGeneratedHtml    = aiGeneratedHtml;
        this.lastAiGeneratedAt  = LocalDateTime.now();
        this.updatedAt          = LocalDateTime.now();
    }

    /** Returns the plain-text version of the AI resume */
    public String getAiGeneratedText() {
        return aiGeneratedText;
    }

    /** Sets the plain-text AI resume */
    public void setAiGeneratedText(String aiGeneratedText) {
        this.aiGeneratedText = aiGeneratedText;
    }

    /** Returns when the AI last generated this resume */
    public LocalDateTime getLastAiGeneratedAt() {
        return lastAiGeneratedAt;
    }

    /** Returns when this resume was first created */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Returns when this resume was last updated */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ================================================================
    //  Functions, Arrays and Strings
    //  Encapsulation: hiding internal comma-string format
    // ================================================================

    /**
     * Converts the comma-separated skills string to a List of strings.
     * Each skill is trimmed and lowercased for consistent comparison.
     *
     * String manipulation: uses split(), trim(), toLowerCase()
     * Arrays: Arrays.stream() converts array to stream
     *
     * Example: "Java, Spring Boot, MySQL" → ["java", "spring boot", "mysql"]
     *
     * @return list of skill strings (lowercase, trimmed), or empty list if none
     */
    public List<String> getSkillsAsList() {
        if (this.skills == null || this.skills.isBlank()) {
            return new ArrayList<>();
        }
        // Split by comma, trim whitespace, convert to lowercase
        String[] skillArray = this.skills.split(",");
        List<String> skillList = new ArrayList<>();
        for (String skill : skillArray) {
            String trimmed = skill.trim().toLowerCase();
            if (!trimmed.isEmpty()) {
                skillList.add(trimmed);
            }
        }
        return skillList;
    }

    /**
     * Sets skills from a List (converts to comma-separated string internally).
     * Arrays and Strings: demonstrates array/list to string conversion.
     *
     * @param skillList list of skill strings
     */
    public void setSkillsFromList(List<String> skillList) {
        if (skillList == null || skillList.isEmpty()) {
            this.skills = "";
            return;
        }
        // Join list elements with ", " separator
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < skillList.size(); i++) {
            builder.append(skillList.get(i).trim());
            if (i < skillList.size() - 1) {
                builder.append(", ");
            }
        }
        this.skills = builder.toString();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks whether this resume contains a specific skill.
     * Case-insensitive comparison.
     * String operations: demonstrates contains/equals on strings.
     *
     * @param skill the skill to search for
     * @return true if the skill is in this resume
     */
    public boolean hasSkill(String skill) {
        if (skill == null || this.skills == null) {
            return false;
        }
        return getSkillsAsList().stream()
                .anyMatch(s -> s.contains(skill.toLowerCase())
                        || skill.toLowerCase().contains(s));
    }

    // ================================================================
    // File Handling & Object Serialization
    // ================================================================

    /**
     * Saves this Resume object to a binary file using Java serialization.
     * Demonstrates ObjectOutputStream for writing objects to files.
     *
     * Uses FileOutputStream + ObjectOutputStream to write
     * the serialized object to disk.
     *
     * @param filePath path to the file to write (e.g., "/tmp/resume_1.dat")
     * @throws IOException if the file cannot be written
     */
    public void saveToFile(String filePath) throws IOException {
        try (FileOutputStream fileOut   = new FileOutputStream(filePath);
             ObjectOutputStream objOut  = new ObjectOutputStream(fileOut)) {
            objOut.writeObject(this);
            System.out.println("[Resume] Saved to file: " + filePath);
        }
    }

    /**
     * Loads a Resume object from a binary file (deserialization).
     * Demonstrates ObjectInputStream for reading objects from files.
     *
     * Uses FileInputStream + ObjectInputStream to read
     * the serialized object from disk.
     * Static method because it creates a new Resume, not modifies an existing one.
     *
     * @param filePath path to the file to read
     * @return the deserialized Resume object
     * @throws IOException            if the file cannot be read
     * @throws ClassNotFoundException if the Resume class is not found during deserialization
     */
    public static Resume loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn  = new FileInputStream(filePath);
             ObjectInputStream objIn = new ObjectInputStream(fileIn)) {
            Resume loaded = (Resume) objIn.readObject();
            System.out.println("[Resume] Loaded from file: " + filePath);
            return loaded;
        }
    }

    /**
     * Exports the resume data as plain text to a file.
     * Uses FileWriter for text file output.
     *
     * @param filePath path to the text file to create
     * @throws IOException if file cannot be written
     */
    public void exportAsTextFile(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("=== RESUME EXPORT ===\n");
            writer.write("Owner:       " + (jobSeeker != null ? jobSeeker.getFullName() : "Unknown") + "\n");
            writer.write("Email:       " + (jobSeeker != null ? jobSeeker.getEmail()    : "Unknown") + "\n");
            writer.write("\nSKILLS:\n");
            writer.write(skills != null ? skills : "Not provided");
            writer.write("\n\nWORK EXPERIENCE:\n");
            writer.write(workExperience != null ? workExperience : "Not provided");
            writer.write("\n\nEDUCATION:\n");
            writer.write(education != null ? education : "Not provided");
            writer.write("\n\nCERTIFICATIONS:\n");
            writer.write(certifications != null ? certifications : "Not provided");
            writer.write("\n\nLANGUAGES: ");
            writer.write(languages != null ? languages : "Not provided");
            writer.write("\n");
            System.out.println("[Resume] Exported as text to: " + filePath);
        }
    }

    /** Returns a string representation of this Resume */
    @Override
    public String toString() {
        return "Resume{"
                + "id=" + id
                + ", owner=" + (jobSeeker != null ? jobSeeker.getFullName() : "none")
                + ", skills='" + (skills != null ? skills.substring(0, Math.min(skills.length(), 50)) : "none") + "'"
                + ", hasAI=" + (aiGeneratedHtml != null)
                + "}";
    }
}