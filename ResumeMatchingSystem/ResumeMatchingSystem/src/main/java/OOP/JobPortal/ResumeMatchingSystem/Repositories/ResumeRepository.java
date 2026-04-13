package OOP.JobPortal.ResumeMatchingSystem.Repositories;

import OOP.JobPortal.ResumeMatchingSystem.Entities.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Database access for the resumes table */
@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    /** Find a resume by its owner's ID */
    Optional<Resume> findByJobSeekerId(Long jobSeekerId);

    /** Check if a resume exists for a given seeker */
    boolean existsByJobSeekerId(Long jobSeekerId);
}
