package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.LoginRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.RegisterEmployerRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.RegisterJobSeekerRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.response.AuthResponse;

public abstract class AbstractAuthService {

    public abstract AuthResponse registerJobSeeker(RegisterJobSeekerRequest req);

    public abstract AuthResponse registerEmployer(RegisterEmployerRequest req);

    public abstract AuthResponse login(LoginRequest req);
}