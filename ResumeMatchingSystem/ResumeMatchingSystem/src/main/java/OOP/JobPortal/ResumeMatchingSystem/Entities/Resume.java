package OOP.JobPortal.ResumeMatchingSystem.Entities;

import jakarta.persistence.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Resume – Composition with JobSeeker.
 * Extended with richer fields: projects, awards, volunteerWork,
 * publications, and references for a comprehensive CV.
 */
@Entity
@Table(name = "resumes")
public class Resume implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    // ── Core resume sections ───────────────────────────────────────

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "work_experience", columnDefinition = "TEXT")
    private String workExperience;

    @Column(name = "education", columnDefinition = "TEXT")
    private String education;

    @Column(name = "certifications", columnDefinition = "TEXT")
    private String certifications;

    @Column(name = "languages")
    private String languages;

    // ── Extended resume sections (new) ─────────────────────────────

    /**
     * Projects the candidate has worked on.
     * Format recommendation: "ProjectName | Tech Stack | Description | Link"
     * One project per line for clarity.
     */
    @Column(name = "projects", columnDefinition = "TEXT")
    private String projects;

    /**
     * Awards and achievements.
     * E.g.: "Best Developer Award 2023 — XYZ Company"
     */
    @Column(name = "awards", columnDefinition = "TEXT")
    private String awards;

    /**
     * Volunteer work and community contributions.
     * E.g.: "Taught programming at local school — 2022–2023"
     */
    @Column(name = "volunteer_work", columnDefinition = "TEXT")
    private String volunteerWork;

    /**
     * Published papers, articles, or blog posts.
     * E.g.: "REST API Best Practices — Medium Blog 2023"
     */
    @Column(name = "publications", columnDefinition = "TEXT")
    private String publications;

    /**
     * Professional references.
     * E.g.: "Ahmed Khan — Manager at Systems Ltd — ahmed@systems.pk"
     */
    @Column(name = "references_text", columnDefinition = "TEXT")
    private String referencesText;

    /**
     * Total years of professional experience.
     * Filled from profile during resume save or entered manually.
     */
    @Column(name = "total_experience_years")
    private Integer totalExperienceYears;

    // ── Links ──────────────────────────────────────────────────────

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "github_url")
    private String githubUrl;

//    // ── AI generation ──────────────────────────────────────────────
//
//    @Column(name = "ai_generated_html", columnDefinition = "LONGTEXT")
//    private String aiGeneratedHtml;
//
//    @Column(name = "ai_generated_text", columnDefinition = "LONGTEXT")
//    private String aiGeneratedText;
//
//    @Column(name = "last_ai_generated_at")
//    private LocalDateTime lastAiGeneratedAt;
//
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Constructors ───────────────────────────────────────────────

    public Resume() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Resume(JobSeeker jobSeeker) {
        this();
        if (jobSeeker == null)
            throw new IllegalArgumentException("Resume must belong to a JobSeeker");
        this.jobSeeker = jobSeeker;
    }

    public Resume(Resume other, JobSeeker newOwner) {
        this(newOwner);
        this.skills          = other.skills;
        this.workExperience  = other.workExperience;
        this.education       = other.education;
        this.certifications  = other.certifications;
        this.languages       = other.languages;
        this.projects        = other.projects;
        this.awards          = other.awards;
        this.volunteerWork   = other.volunteerWork;
        this.publications    = other.publications;
    }

    // ── Lifecycle ──────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Skills helpers ─────────────────────────────────────────────

    public List<String> getSkillsAsList() {
        if (this.skills == null || this.skills.isBlank()) return new ArrayList<>();
        String[] arr = this.skills.split(",");
        List<String> list = new ArrayList<>();
        for (String s : arr) {
            String t = s.trim().toLowerCase();
            if (!t.isEmpty()) list.add(t);
        }
        return list;
    }

    public void setSkillsFromList(List<String> skillList) {
        if (skillList == null || skillList.isEmpty()) { this.skills = ""; return; }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < skillList.size(); i++) {
            sb.append(skillList.get(i).trim());
            if (i < skillList.size() - 1) sb.append(", ");
        }
        this.skills = sb.toString();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasSkill(String skill) {
        if (skill == null || this.skills == null) return false;
        return getSkillsAsList().stream()
                .anyMatch(s -> s.contains(skill.toLowerCase()) || skill.toLowerCase().contains(s));
    }

    // ── File handling ──────────────────────────────────────────────

    public void saveToFile(String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(this);
        }
    }

    public static Resume loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Resume) in.readObject();
        }
    }

    public void exportAsTextFile(String filePath) throws IOException {
        try (FileWriter w = new FileWriter(filePath)) {
            w.write("=== RESUME EXPORT ===\n");
            w.write("Owner: " + (jobSeeker != null ? jobSeeker.getFullName() : "Unknown") + "\n");
            w.write("\nSKILLS:\n" + nvl(skills));
            w.write("\n\nWORK EXPERIENCE:\n" + nvl(workExperience));
            w.write("\n\nEDUCATION:\n" + nvl(education));
            w.write("\n\nPROJECTS:\n" + nvl(projects));
            w.write("\n\nCERTIFICATIONS:\n" + nvl(certifications));
            w.write("\n\nAWARDS:\n" + nvl(awards));
            w.write("\n\nLANGUAGES: " + nvl(languages));
            w.write("\n\nVOLUNTEER WORK:\n" + nvl(volunteerWork));
            w.write("\n\nPUBLICATIONS:\n" + nvl(publications));
            w.write("\n\nREFERENCES:\n" + nvl(referencesText));
        }
    }

    private String nvl(String s) { return s != null ? s : "Not provided"; }

    // ── Getters & Setters ──────────────────────────────────────────

    public Long getId()                                          { return id; }
    public JobSeeker getJobSeeker()                              { return jobSeeker; }
    public void setJobSeeker(JobSeeker js)                       { this.jobSeeker = js; }
    public String getSkills()                                    { return skills; }
    public void setSkills(String s)                              { this.skills = s; this.updatedAt = LocalDateTime.now(); }
    public String getWorkExperience()                            { return workExperience; }
    public void setWorkExperience(String s)                      { this.workExperience = s; this.updatedAt = LocalDateTime.now(); }
    public String getEducation()                                 { return education; }
    public void setEducation(String s)                           { this.education = s; this.updatedAt = LocalDateTime.now(); }
    public String getCertifications()                            { return certifications; }
    public void setCertifications(String s)                      { this.certifications = s; }
    public String getLanguages()                                 { return languages; }
    public void setLanguages(String s)                           { this.languages = s; }
    public String getProjects()                                  { return projects; }
    public void setProjects(String s)                            { this.projects = s; }
    public String getAwards()                                    { return awards; }
    public void setAwards(String s)                              { this.awards = s; }
    public String getVolunteerWork()                             { return volunteerWork; }
    public void setVolunteerWork(String s)                       { this.volunteerWork = s; }
    public String getPublications()                              { return publications; }
    public void setPublications(String s)                        { this.publications = s; }
    public String getReferencesText()                            { return referencesText; }
    public void setReferencesText(String s)                      { this.referencesText = s; }
    public Integer getTotalExperienceYears()                     { return totalExperienceYears; }
    public void setTotalExperienceYears(Integer y)               { this.totalExperienceYears = y; }
    public String getPortfolioUrl()                              { return portfolioUrl; }
    public void setPortfolioUrl(String s)                        { this.portfolioUrl = s; }
    public String getLinkedinUrl()                               { return linkedinUrl; }
    public void setLinkedinUrl(String s)                         { this.linkedinUrl = s; }
    public String getGithubUrl()                                 { return githubUrl; }
    public void setGithubUrl(String s)                           { this.githubUrl = s; }
//    public String getAiGeneratedHtml()                           { return aiGeneratedHtml; }
//    public void setAiGeneratedHtml(String s)                     { this.aiGeneratedHtml = s; this.lastAiGeneratedAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now(); }
//    public String getAiGeneratedText()                           { return aiGeneratedText; }
//    public void setAiGeneratedText(String s)                     { this.aiGeneratedText = s; }
//    public LocalDateTime getLastAiGeneratedAt()                  { return lastAiGeneratedAt; }
    public LocalDateTime getCreatedAt()                          { return createdAt; }
    public LocalDateTime getUpdatedAt()                          { return updatedAt; }

    @Override
    public String toString() {
        return "Resume{id=" + id + ", owner=" + (jobSeeker != null ? jobSeeker.getFullName() : "none") + "}";
    }
}