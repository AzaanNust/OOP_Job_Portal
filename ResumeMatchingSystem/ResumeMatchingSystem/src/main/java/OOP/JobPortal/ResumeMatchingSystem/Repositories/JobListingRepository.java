package OOP.JobPortal.ResumeMatchingSystem.Repositories;

import OOP.JobPortal.ResumeMatchingSystem.Entities.JobListing;
import OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus;
import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JobListingRepository – data access for job_listings table.
 *
 * WEEK 3 – Functionality (the main search/filter feature):
 *   searchJobs() implements the multi-filter job search.
 *   All three filters (title, location, shift) are optional.
 *   If null, that filter is simply not applied.
 */
@Repository
public interface JobListingRepository extends JpaRepository<JobListing, Long> {

    /**
     * Search open jobs with optional filters.
     *
     * @Query uses JPQL (Java Persistence Query Language) — like SQL but uses
     * class names instead of table names.
     *
     * :title    IS NULL → if no title filter, skip that condition
     * LIKE '%' + :title + '%' → partial match anywhere in the title
     *
     * @param title    partial job title to search (null = no filter)
     * @param location partial city name to search (null = no filter)
     * @param shift    exact shift type to filter (null = no filter)
     * @param pageable pagination settings (page number, page size, sort)
     * @return         paginated list of matching open jobs
     */
    @Query("SELECT j FROM JobListing j WHERE " +
            "j.status = OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus.OPEN AND " + // Fixed 'Enums'
            "(:title    IS NULL OR LOWER(j.title)    LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:shift    IS NULL OR j.shiftType = :shift)")
    Page<JobListing> searchJobs(
            @Param("title")    String title,
            @Param("location") String location,
            @Param("shift")    ShiftType shift,
            Pageable pageable
    );

    /** All jobs posted by a specific employer, newest first */
    List<JobListing> findByEmployerIdOrderByCreatedAtDesc(Long employerId);

    /** All open jobs (for homepage listing) */
    Page<JobListing> findByStatusOrderByCreatedAtDesc(JobStatus status, Pageable pageable);
}