package OOP.JobPortal.ResumeMatchingSystem.DTOs.response;
import OOP.JobPortal.ResumeMatchingSystem.Enums.UserRole;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/** Returned after successful login or registration. Contains JWT token + user info. */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AuthResponse {
    private String token;
    private Long userId;
    private String fullName;
    private String email;
    private UserRole role;
    public AuthResponse() {}
    public AuthResponse(String token, Long userId, String fullName, String email, UserRole role) {
        this.token = token; this.userId = userId; this.fullName = fullName;
        this.email = email; this.role = role;
    }
//    public String getToken() { return token; }
//    public void setToken(String token) { this.token = token; }
//    public Long getUserId() { return userId; }
//    public void setUserId(Long userId) { this.userId = userId; }
//    public String getFullName() { return fullName; }
//    public void setFullName(String fullName) { this.fullName = fullName; }
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
//    public UserRole getRole() { return role; }
//    public void setRole(UserRole role) { this.role = role; }
}
