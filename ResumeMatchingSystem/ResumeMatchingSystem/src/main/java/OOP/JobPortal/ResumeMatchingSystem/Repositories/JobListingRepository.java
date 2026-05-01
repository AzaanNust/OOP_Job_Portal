package OOP.JobPortal.ResumeMatchingSystem.Repositories;

import OOP.JobPortal.ResumeMatchingSystem.Entities.JobListing;
import OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus;
import OOP.JobPortal.ResumeMatchingSystem.Enums.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobListingRepository extends JpaRepository<JobListing, Long> {

    /**
     * Search open jobs with optional filters — returns a plain List.
     *
     * Using List instead of Page avoids Spring's PageImpl serialization
     * instability which caused Gson on Android to silently return null,
     * making the job list appear empty even when jobs exist in the database.
     */
    @Query("SELECT j FROM JobListing j WHERE " +
            "j.status = OOP.JobPortal.ResumeMatchingSystem.Enums.JobStatus.OPEN AND " +
            "(:title    IS NULL OR LOWER(j.title)    LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:shift    IS NULL OR j.shiftType = :shift) " +
            "ORDER BY j.createdAt DESC")
    List<JobListing> searchJobsList(
            @Param("title")    String title,
            @Param("location") String location,
            @Param("shift")    ShiftType shift
    );

    /** All jobs posted by a specific employer, newest first */
    List<JobListing> findByEmployerIdOrderByCreatedAtDesc(Long employerId);

    /** All open jobs */
    List<JobListing> findByStatusOrderByCreatedAtDesc(JobStatus status);
}