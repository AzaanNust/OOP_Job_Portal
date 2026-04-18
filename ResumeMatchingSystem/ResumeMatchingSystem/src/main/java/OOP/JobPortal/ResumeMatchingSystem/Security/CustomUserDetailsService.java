package OOP.JobPortal.ResumeMatchingSystem.Security;

import OOP.JobPortal.ResumeMatchingSystem.Repositories.AdminRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.EmployerRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.JobSeekerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService – Loads a user from the database by email.
 *
 * Spring Security calls loadUserByUsername() when it needs to find a user
 * for authentication. Since we have three separate tables (job_seekers,
 * employers, admins), we must search all three.
 *
 * WEEK 8 – Interface Implementation:
 *   Implements UserDetailsService interface from Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JobSeekerRepository jobSeekerRepo;

    @Autowired
    private EmployerRepository employerRepo;

    @Autowired
    private AdminRepository adminRepo;

    /**
     * Searches all three user tables for a user with the given email.
     *
     * @param email the email address to look up (acts as username)
     * @return      the UserDetails for the found user
     * @throws UsernameNotFoundException if no user exists with this email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Search job_seekers table first
        var jobSeeker = jobSeekerRepo.findByEmail(email);
        if (jobSeeker.isPresent()) {
            return jobSeeker.get();
        }

        // Search employers table next
        var employer = employerRepo.findByEmail(email);
        if (employer.isPresent()) {
            return employer.get();
        }

        // Search admins table last
        var admin = adminRepo.findByEmail(email);
        if (admin.isPresent()) {
            return admin.get();
        }

        // No user found in any table
        throw new UsernameNotFoundException("No account found with email: " + email);
    }
}

