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
 * POSTGRESQL FIX:
 *   When a String parameter is null, the PostgreSQL JDBC driver sends it
 *   with type 'bytea' by default. Then LOWER(:title) becomes LOWER(bytea)
 *   which doesn't exist in PostgreSQL → "function lower(bytea) does not exist".
 *
 *   The fix is CAST(:title AS string) in JPQL — this tells Hibernate to
 *   declare the parameter type explicitly, so PostgreSQL receives it as TEXT.
 */
@Repository
public interface JobListingRepository extends JpaRepository<JobListing, Long> {

    /** Search open jobs WITH a specific shift filter. */
    @Query("SELECT j FROM JobListing j WHERE " +
            "j.status = OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus.OPEN AND " +
            "(CAST(:title    AS string) IS NULL OR LOWER(j.title)    LIKE LOWER(CONCAT('%', CAST(:title    AS string), '%'))) AND " +
            "(CAST(:location AS string) IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', CAST(:location AS string), '%'))) AND " +
            "j.shiftType = :shift " +
            "ORDER BY j.createdAt DESC")
    List<JobListing> searchJobsWithShift(
            @Param("title")    String title,
            @Param("location") String location,
            @Param("shift")    ShiftType shift
    );

    /** Search open jobs WITHOUT shift filter (any shift). */
    @Query("SELECT j FROM JobListing j WHERE " +
            "j.status = OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus.OPEN AND " +
            "(CAST(:title    AS string) IS NULL OR LOWER(j.title)    LIKE LOWER(CONCAT('%', CAST(:title    AS string), '%'))) AND " +
            "(CAST(:location AS string) IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', CAST(:location AS string), '%'))) " +
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