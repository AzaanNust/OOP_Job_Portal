package OOP.JobPortal.ResumeMatchingSystem.Repositories;

import OOP.JobPortal.ResumeMatchingSystem.Entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Database access for the applications table */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /** All applications by a seeker, newest first */
    List<Application> findByJobSeekerIdOrderByAppliedAtDesc(Long jobSeekerId);

    /** All applications for a job, sorted by match score (highest first) */
    List<Application> findByJobListingIdOrderByMatchScoreDesc(Long jobListingId);

    /** Check if a seeker already applied to a specific job */
    boolean existsByJobSeekerIdAndJobListingId(Long jobSeekerId, Long jobListingId);

//    /** Find one specific application by seeker + job */
//    Optional<Application> findByJobSeekerIdAndJobListingId(Long jobSeekerId, Long jobListingId);
//
//    /** Find all applications for all jobs posted by an employer */
//    @Query("SELECT a FROM Application a WHERE a.jobListing.employer.id = :employerId ORDER BY a.matchScore DESC")
//    List<Application> findByEmployerId(@Param("employerId") Long employerId);
}
