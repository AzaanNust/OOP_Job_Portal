package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.Entities.JobListing;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Resume;

import java.util.List;

/**
 * ============================================================
 * MatchingStrategy  –  Interface (Week 8)
 * ============================================================
 *
 * WEEK 8 – Defining and Implementing Interfaces:
 *   An interface is a pure contract — it defines WHAT must be done,
 *   but NOT how it is done. Any class that 'implements' this interface
 *   must provide concrete implementations for all its methods.
 *
 * WEEK 8 – Interface vs Abstract Class:
 *   Interface:      only method signatures (no state, no implementation)
 *   Abstract Class: can have state + some concrete methods + abstract methods
 *
 *   Use Interface when:  you only need a contract (plug-in pattern)
 *   Use Abstract Class when: subclasses share some common behaviour/state
 *
 * WEEK 13 – SOLID Principles:
 *   Open-Closed Principle (OCP):
 *     You can add a new matching algorithm (e.g., AI-based, experience-weighted)
 *     by creating a new class that implements this interface — without modifying
 *     any existing code. The system is OPEN for extension, CLOSED for modification.
 *
 *   Dependency Inversion Principle (DIP):
 *     High-level modules (ApplicationService) depend on this abstraction.
 *     Low-level modules (SkillBasedMatcher) implement this abstraction.
 *     This means ApplicationService never imports SkillBasedMatcher directly.
 *
 * STRATEGY DESIGN PATTERN:
 *   This interface is the core of the Strategy pattern.
 *   The algorithm (how to calculate match score) is interchangeable.
 *   You could swap SkillBasedMatcher for an AI-powered matcher
 *   without changing ApplicationService at all.
 * ============================================================
 */
public interface MatchingStrategy {

    /**
     * Calculates how well a resume matches a job listing.
     *
     * SCORING CONTRACT:
     *   - Returns a value between 0.0 (no match) and 100.0 (perfect match)
     *   - Score MUST be deterministic: same inputs → same output
     *   - Score MUST NOT modify either parameter
     *
     * @param resume      the candidate's resume with skills and experience
     * @param jobListing  the job posting with required and preferred skills
     * @return            compatibility score from 0.0 to 100.0
     */
    double calculateScore(Resume resume, JobListing jobListing);

    /**
     * Returns a list of skills the candidate should learn to be
     * a stronger applicant for this specific job.
     *
     * CONTRACT:
     *   - Returns only skills from the job that the resume is missing
     *   - Returns empty list if the candidate has all required skills
     *   - Required skills are listed before preferred skills
     *
     * @param resume      the candidate's resume
     * @param jobListing  the job posting
     * @return            list of skill names to improve (may be empty)
     */
    List<String> getSkillsToImprove(Resume resume, JobListing jobListing);
}
