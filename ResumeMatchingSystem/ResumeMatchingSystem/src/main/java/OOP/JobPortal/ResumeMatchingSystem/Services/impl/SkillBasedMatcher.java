package OOP.JobPortal.ResumeMatchingSystem.Services.impl;


import OOP.JobPortal.ResumeMatchingSystem.Entities.JobListing;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Resume;
import OOP.JobPortal.ResumeMatchingSystem.Services.MatchingStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * SkillBasedMatcher  –  Implements MatchingStrategy (Week 8)
 * ============================================================
 *
 * WEEK 8 – Implementing Interfaces:
 *   Uses the 'implements' keyword to fulfill the MatchingStrategy contract.
 *   MUST provide concrete implementations of calculateScore() and
 *   getSkillsToImprove() — otherwise the code won't compile.
 *
 * WEEK 13 – SOLID / Open-Closed Principle:
 *   This class is a plug-in implementation.
 *   A future AI-based matcher can be added without touching this class.
 *
 * WEEK 4 – Method Overloading:
 *   containsSkill() has two overloads:
 *     containsSkill(List, String)        → exact + partial matching
 *     containsSkill(List, String, boolean) → allows toggling fuzzy matching
 *
 * SCORING ALGORITHM:
 *   Required skills matched:   70 points maximum (most important)
 *   Preferred skills matched:  20 points maximum (nice-to-have)
 *   Experience years matched:  10 points maximum
 *   ─────────────────────────────────────────
 *   Total:                    100 points maximum
 *
 * @Component: Spring creates one instance of this class and
 *   injects it wherever MatchingStrategy is needed.
 * ============================================================
 */
@Component
public class SkillBasedMatcher extends MatchingStrategy {

    /**
     * WEEK 4 – Static Class Member:
     * Maximum possible score. Defined as a constant so it is never
     * accidentally changed and is easy to find if the algorithm changes.
     */
    private static final double MAX_SCORE = 100.0;

    /**
     * Weight assigned to required skills (70% of total score).
     * Defined as constants to make the algorithm transparent.
     */
    private static final double REQUIRED_SKILLS_WEIGHT  = 70.0;
    private static final double PREFERRED_SKILLS_WEIGHT = 20.0;
    private static final double EXPERIENCE_WEIGHT       = 10.0;

    /**
     * Calculates a compatibility score between a resume and a job listing.
     *
     * WEEK 8 – implements MatchingStrategy:
     *   This is the concrete implementation of the interface method.
     *
     * @param resume      the candidate's resume (source of skills + experience)
     * @param jobListing  the job posting (target requirements)
     * @return            score from 0.0 to 100.0 (capped at MAX_SCORE)
     */
    @Override
    public double calculateScore(Resume resume, JobListing jobListing) {
        // Guard: null inputs produce a zero score
        if (resume == null || jobListing == null) {
            return 0.0;
        }

        // Extract skills as Lists for easy comparison
        List<String> candidateSkills = resume.getSkillsAsList();
        List<String> requiredSkills  = jobListing.getRequiredSkillsAsList();
        List<String> preferredSkills = jobListing.getPreferredSkillsAsList();

        // ── 1. Required Skills Score (70 points max) ──────────────
        double requiredScore;
        if (requiredSkills.isEmpty()) {
            // Job has no required skills → award full points
            requiredScore = REQUIRED_SKILLS_WEIGHT;
        } else {
            // Count how many required skills the candidate has
            int matchedRequired = 0;
            for (String requiredSkill : requiredSkills) {
                if (containsSkill(candidateSkills, requiredSkill)) {
                    matchedRequired++;
                }
            }
            // Calculate proportional score
            requiredScore = ((double) matchedRequired / requiredSkills.size())
                    * REQUIRED_SKILLS_WEIGHT;
        }

        // ── 2. Preferred Skills Score (20 points max) ─────────────
        double preferredScore;
        if (preferredSkills.isEmpty()) {
            preferredScore = PREFERRED_SKILLS_WEIGHT;
        } else {
            int matchedPreferred = 0;
            for (String preferredSkill : preferredSkills) {
                if (containsSkill(candidateSkills, preferredSkill)) {
                    matchedPreferred++;
                }
            }
            preferredScore = ((double) matchedPreferred / preferredSkills.size())
                    * PREFERRED_SKILLS_WEIGHT;
        }

        // ── 3. Experience Score (10 points max) ───────────────────
        double experienceScore;
        int minRequired  = jobListing.getMinExperienceYears();
        int candidateExp = resume.getJobSeeker().getTotalExperienceYears();

        if (minRequired <= 0) {
            // No experience requirement → full points
            experienceScore = EXPERIENCE_WEIGHT;
        } else if (candidateExp >= minRequired) {
            // Candidate meets or exceeds the requirement → full points
            experienceScore = EXPERIENCE_WEIGHT;
        } else if (candidateExp > 0) {
            // Partial credit for some experience
            experienceScore = ((double) candidateExp / minRequired) * EXPERIENCE_WEIGHT;
        } else {
            // Zero experience when some is required → zero experience points
            experienceScore = 0.0;
        }

        // ── Total: sum all three components, cap at 100 ───────────
        double total = requiredScore + preferredScore + experienceScore;

        // Round to one decimal place for clean display
        double rounded = Math.round(total * 10.0) / 10.0;

        return Math.min(rounded, MAX_SCORE);
    }

    /**
     * Returns the list of skills the candidate should learn to be
     * a stronger candidate for this specific job.
     *
     * WEEK 8 – implements MatchingStrategy:
     *   Concrete implementation of the interface method.
     *
     * @param resume      candidate's resume
     * @param jobListing  the job posting
     * @return            list of missing/weak skills (may be empty)
     */
    @Override
    public List<String> getSkillsToImprove(Resume resume, JobListing jobListing) {
        if (resume == null || jobListing == null) {
            return new ArrayList<>();
        }

        List<String> candidateSkills = resume.getSkillsAsList();
        List<String> missing         = new ArrayList<>();

        // First: missing REQUIRED skills (most important to learn)
        for (String skill : jobListing.getRequiredSkillsAsList()) {
            if (!containsSkill(candidateSkills, skill)) {
                missing.add(skill);
            }
        }

        // Then: missing PREFERRED skills (nice-to-have — limit to 5)
        int preferredAdded = 0;
        for (String skill : jobListing.getPreferredSkillsAsList()) {
            if (preferredAdded >= 5) {
                break;  // Don't overwhelm the user with too many suggestions
            }
            if (!containsSkill(candidateSkills, skill)) {
                // Mark as preferred so the frontend can style them differently
                missing.add(skill + " (preferred)");
                preferredAdded++;
            }
        }

        return missing;
    }

    // ================================================================
    // WEEK 4 – Method Overloading:
    //   containsSkill() has two versions:
    //   Version 1: always uses fuzzy matching
    //   Version 2: allows the caller to choose strict vs fuzzy matching
    // ================================================================

    /**
     * Checks whether a skill appears in the candidate's skill list.
     * Uses fuzzy matching: "spring" matches "spring boot".
     *
     * WEEK 2 – Strings: demonstrates .contains() for partial matching
     *
     * @param candidateSkills list of skills from the resume
     * @param targetSkill     the skill to look for
     * @return                true if the skill is found (partial match counts)
     */
    private boolean containsSkill(List<String> candidateSkills, String targetSkill) {
        // Delegate to the overloaded version with fuzzy=true
        return containsSkill(candidateSkills, targetSkill, true);
    }

    /**
     * WEEK 4 – Method Overloading: overloaded version with fuzzy flag.
     * Allows choosing between strict matching and fuzzy (partial) matching.
     *
     * Strict:  "spring boot" must exactly equal "spring boot"
     * Fuzzy:   "spring" matches "spring boot", "spring framework", etc.
     *
     * @param candidateSkills list of skills from the resume
     * @param targetSkill     the skill to look for
     * @param fuzzy           true = partial match allowed; false = exact match only
     * @return                true if the skill is found
     */
    private boolean containsSkill(List<String> candidateSkills,
                                  String targetSkill,
                                  boolean fuzzy) {
        if (candidateSkills == null || targetSkill == null) {
            return false;
        }

        String target = targetSkill.toLowerCase().trim();

        for (String candidateSkill : candidateSkills) {
            String candidate = candidateSkill.toLowerCase().trim();

            if (fuzzy) {
                // Fuzzy: either one contains the other
                if (candidate.contains(target) || target.contains(candidate)) {
                    return true;
                }
            } else {
                // Strict: exact match only
                if (candidate.equals(target)) {
                    return true;
                }
            }
        }
        return false;
    }
}

