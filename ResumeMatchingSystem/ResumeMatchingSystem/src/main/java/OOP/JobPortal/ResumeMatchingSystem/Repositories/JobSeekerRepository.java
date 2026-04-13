package OOP.JobPortal.ResumeMatchingSystem.Repositories;

import OOP.JobPortal.ResumeMatchingSystem.Entities.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JobSeekerRepository – database access for the job_seekers table.
 *
 * WEEK 3 – Functionality (save/load data):
 *   JpaRepository<JobSeeker, Long> gives us:
 *     save()     – insert or update a row
 *     findById() – SELECT by primary key
 *     findAll()  – SELECT all rows
 *     delete()   – DELETE a row
 *     count()    – COUNT(*)
 *
 * Spring Data JPA generates SQL automatically from method names.
 * findByEmail → SELECT * FROM job_seekers WHERE email = ?
 */
@Repository
public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long> {

    /** Find a seeker by their email address (used during login) */
    Optional<JobSeeker> findByEmail(String email);

    /** Check if an email is already registered (prevent duplicates) */
    boolean existsByEmail(String email);
}