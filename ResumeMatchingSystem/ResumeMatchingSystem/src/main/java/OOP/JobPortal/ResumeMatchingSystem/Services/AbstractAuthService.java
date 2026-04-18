package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.dto.request.LoginRequest;
import OOP.JobPortal.ResumeMatchingSystem.dto.request.RegisterEmployerRequest;
import OOP.JobPortal.ResumeMatchingSystem.dto.request.RegisterJobSeekerRequest;
import OOP.JobPortal.ResumeMatchingSystem.dto.response.AuthResponse;

public abstract class AbstractAuthService {

    public abstract AuthResponse registerJobSeeker(RegisterJobSeekerRequest req);

    public abstract AuthResponse registerEmployer(RegisterEmployerRequest req);

    public abstract AuthResponse login(LoginRequest req);
}