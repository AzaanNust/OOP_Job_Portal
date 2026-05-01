package OOP.JobPortal.ResumeMatchingSystem.Repositories;

import OOP.JobPortal.ResumeMatchingSystem.Entities.JobListing;
import OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus;
import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JobListingRepository – data access for job_listings table.
 *
 * Two query methods to handle the optional shift filter cleanly:
 *   - searchJobsWithShift()    when shift is provided
 *   - searchJobsWithoutShift() when shift is null
 *
 * This avoids PostgreSQL's strict type checking on nullable enum parameters,
 * while still using the enum directly (no String conversion needed).
 *
 * The status comparison uses the fully-qualified enum constant
 * (OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus.OPEN) which JPQL
 * accepts on both PostgreSQL and MySQL.
 */
@Repository
public interface JobListingRepository extends JpaRepository<JobListing, Long> {

    /** Search open jobs with shift filter (shift must NOT be null). */
    @Query("SELECT j FROM JobListing j WHERE " +
            "j.status = OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus.OPEN AND " +
            "(:title    IS NULL OR LOWER(j.title)    LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "j.shiftType = :shift " +
            "ORDER BY j.createdAt DESC")
    List<JobListing> searchJobsWithShift(
            @Param("title")    String title,
            @Param("location") String location,
            @Param("shift")    ShiftType shift
    );

    /** Search open jobs without shift filter (any shift). */
    @Query("SELECT j FROM JobListing j WHERE " +
            "j.status = OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus.OPEN AND " +
            "(:title    IS NULL OR LOWER(j.title)    LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "ORDER BY j.createdAt DESC")
    List<JobListing> searchJobsWithoutShift(
            @Param("title")    String title,
            @Param("location") String location
    );

    /** All jobs posted by a specific employer, newest first */
    List<JobListing> findByEmployerIdOrderByCreatedAtDesc(Long employerId);

    /** All jobs by status */
    List<JobListing> findByStatusOrderByCreatedAtDesc(JobStatus status);
}