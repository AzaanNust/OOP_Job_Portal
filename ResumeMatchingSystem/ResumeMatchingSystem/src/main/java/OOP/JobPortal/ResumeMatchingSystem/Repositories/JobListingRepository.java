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
 * IMPORTANT: PostgreSQL does NOT allow nullable enum parameters in JPQL like MySQL does.
 * The fix is to convert the enum to a String on the calling side and compare both
 * sides as strings using CAST. This works on both PostgreSQL and MySQL.
 */
@Repository
public interface JobListingRepository extends JpaRepository<JobListing, Long> {

    /**
     * Search open jobs with optional filters.
     * Returns a plain List — avoids Spring PageImpl JSON serialization issues with Gson.
     *
     * @param title         partial job title (null = no filter)
     * @param location      partial city name (null = no filter)
     * @param shiftString   shift type name as String e.g. "NIGHT" (null = no filter)
     */
    @Query("SELECT j FROM JobListing j WHERE " +
            "j.status = OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus.OPEN AND " +
            "(:title    IS NULL OR LOWER(j.title)    LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:shiftString IS NULL OR CAST(j.shiftType AS string) = :shiftString) " +
            "ORDER BY j.createdAt DESC")
    List<JobListing> searchJobsList(
            @Param("title")       String title,
            @Param("location")    String location,
            @Param("shiftString") String shiftString
    );

    /** All jobs posted by a specific employer, newest first */
    List<JobListing> findByEmployerIdOrderByCreatedAtDesc(Long employerId);

    /** All jobs by status */
    List<JobListing> findByStatusOrderByCreatedAtDesc(JobStatus status);
}