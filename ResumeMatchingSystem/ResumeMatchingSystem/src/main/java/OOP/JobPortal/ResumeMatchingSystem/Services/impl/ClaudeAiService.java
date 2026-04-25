package OOP.JobPortal.ResumeMatchingSystem.Services.impl;

import OOP.JobPortal.ResumeMatchingSystem.Entities.JobSeeker;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Resume;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * ClaudeAiService  –  AI Resume Generation
 * ============================================================
 *
 * Calls the Anthropic Claude API to generate a professional,
 * ATS-friendly HTML resume from the seeker's raw profile data.
 *
 * WEEK 13 – Single Responsibility Principle:
 *   This service only does ONE thing: call the Claude API.
 *   It does not save the resume or generate PDFs — those are
 *   handled by ResumeService and PdfService respectively.
 *
 * WEEK 12 – Exception Handling:
 *   If the AI API fails (no internet, invalid key, rate limit),
 *   this service catches the exception and falls back to a
 *   template-based HTML resume — the app never completely breaks.
 *
 * WEEK 4 – Method Overloading:
 *   buildResumePrompt() has two overloads:
 *     buildResumePrompt(JobSeeker, Resume)        → full AI prompt
 *     buildResumePrompt(JobSeeker, Resume, String) → with custom instructions
 * ============================================================
 */
@Service
public class ClaudeAiService {

    /** Claude API key from application.yml */
    @Value("${claude.api.key}")
    private String apiKey;

    /** Claude API endpoint URL */
    @Value("${claude.api.url}")
    private String apiUrl;

    /** Which Claude model to use */
    @Value("${claude.model}")
    private String model;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Generates an HTML resume for the given seeker using Claude AI.
     *
     * WEEK 12 – Exception Handling (try-catch):
     *   If the API call fails for any reason, we catch the exception
     *   and return a basic fallback resume instead of crashing.
     *
     * @param seeker the job seeker whose profile to convert
     * @param resume the resume with skills, experience, education
     * @return       AI-generated HTML resume string
     */
    public String generateResumeHtml(JobSeeker seeker, Resume resume) {

        // Build the detailed prompt for Claude
        String prompt = buildResumePrompt(seeker, resume);

        // Build the API request body as a Map (will be serialized to JSON)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", 4000);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        requestBody.put("messages", messages);

        try {
            // Make the HTTP POST call to the Claude API
            WebClient client = webClientBuilder.build();

            String responseJson = client.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();  // Block for synchronous response

            // Parse the JSON response to extract the generated text
            JsonNode root     = objectMapper.readTree(responseJson);
            JsonNode content  = root.path("content");
            String generated  = content.get(0).path("text").asText();

            System.out.println("[ClaudeAI] Resume generated successfully for: "
                    + seeker.getEmail());

            return generated;

        } catch (Exception e) {
            // WEEK 12 – Catch block: handle API failure gracefully
            System.err.println("[ClaudeAI] API call failed: " + e.getMessage()
                    + " — using fallback template");

            // Return a basic template resume instead of failing completely
            return generateFallbackHtml(seeker, resume);
        }
    }

    // ================================================================
    // WEEK 4 – Method Overloading:
    //   Two buildResumePrompt() methods — same name, different parameters.
    // ================================================================

    /**
     * Builds the AI prompt with default instructions.
     *
     * @param seeker the job seeker
     * @param resume the resume data
     * @return       the full prompt string to send to Claude
     */
    private String buildResumePrompt(JobSeeker seeker, Resume resume) {
        // Delegate to the overloaded version with a default instruction
        return buildResumePrompt(seeker, resume,
                "Make it professional, ATS-friendly, and suitable for the Pakistani job market.");
    }

    /**
     * WEEK 4 – Method Overloading: overloaded version with custom instructions.
     * Allows callers to provide additional guidance to the AI.
     *
     * @param seeker              the job seeker
     * @param resume              the resume data
     * @param customInstructions  additional instructions for the AI
     * @return                    the full prompt string
     */
    private String buildResumePrompt(JobSeeker seeker, Resume resume,
                                     String customInstructions) {

        // WEEK 2 – Strings: StringBuilder for efficient string concatenation
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a professional resume writer. ");
        prompt.append("Generate a complete, professional HTML resume for the candidate below. ");
        prompt.append("Use ONLY inline CSS. No external stylesheets. ");
        prompt.append("Use a clean design with navy blue (#1a3c6e) headers. ");
        prompt.append(customInstructions).append("\n\n");

        prompt.append("CANDIDATE PROFILE:\n");
        prompt.append("Name:       ").append(seeker.getFullName()).append("\n");
        prompt.append("Email:      ").append(seeker.getEmail()).append("\n");
        prompt.append("Phone:      ").append(nullOrValue(seeker.getPhoneNumber())).append("\n");
        prompt.append("Location:   ").append(nullOrValue(seeker.getPreferredLocation())).append("\n");
        prompt.append("Experience: ").append(seeker.getTotalExperienceYears()).append(" years\n");
        prompt.append("Summary:    ").append(nullOrValue(seeker.getProfileSummary())).append("\n\n");

        prompt.append("SKILLS:\n").append(nullOrValue(resume.getSkills())).append("\n\n");
        prompt.append("WORK EXPERIENCE:\n").append(nullOrValue(resume.getWorkExperience())).append("\n\n");
        prompt.append("EDUCATION:\n").append(nullOrValue(resume.getEducation())).append("\n\n");
        prompt.append("CERTIFICATIONS:\n").append(nullOrValue(resume.getCertifications())).append("\n\n");
        prompt.append("LANGUAGES: ").append(nullOrValue(resume.getLanguages())).append("\n");
        prompt.append("LinkedIn:  ").append(nullOrValue(resume.getLinkedinUrl())).append("\n");
        prompt.append("GitHub:    ").append(nullOrValue(resume.getGithubUrl())).append("\n\n");

        prompt.append("IMPORTANT: Return ONLY the HTML document. ");
        prompt.append("No markdown, no explanation text, just pure HTML.");

        return prompt.toString();
    }

    /**
     * Generates a basic HTML resume without AI when the API is unavailable.
     * This ensures the feature degrades gracefully.
     *
     * @param seeker the job seeker
     * @param resume the resume data
     * @return       basic HTML resume string
     */
    private String generateFallbackHtml(JobSeeker seeker, Resume resume) {

        // Build skill badges HTML
        StringBuilder skillBadges = new StringBuilder();
        for (String skill : resume.getSkillsAsList()) {
            skillBadges.append("<span style='display:inline-block;background:#e8f0fe;"
                    + "color:#1a3c6e;padding:3px 10px;border-radius:12px;margin:3px;"
                    + "font-size:12px;'>").append(skill.trim()).append("</span>");
        }

        return "<!DOCTYPE html><html><head><style>"
                + "body{font-family:Arial,sans-serif;margin:40px;color:#333;}"
                + "h1{color:#1a3c6e;border-bottom:2px solid #1a3c6e;padding-bottom:8px;}"
                + "h2{color:#1a3c6e;font-size:14px;text-transform:uppercase;margin-top:20px;}"
                + "p{font-size:13px;line-height:1.6;}"
                + "</style></head><body>"
                + "<h1>" + escapeHtml(seeker.getFullName()) + "</h1>"
                + "<p>" + escapeHtml(seeker.getEmail()) + " | "
                + escapeHtml(nullOrValue(seeker.getPhoneNumber())) + " | "
                + escapeHtml(nullOrValue(seeker.getPreferredLocation())) + "</p>"
                + "<h2>Skills</h2><div>" + skillBadges + "</div>"
                + "<h2>Work Experience</h2><p>" + escapeHtml(nullOrValue(resume.getWorkExperience())) + "</p>"
                + "<h2>Education</h2><p>" + escapeHtml(nullOrValue(resume.getEducation())) + "</p>"
                + "</body></html>";
    }

    /**
     * Returns a "Not provided" placeholder if the value is null or blank.
     *
     * @param value the string to check
     * @return      the value, or "Not provided" if null/blank
     */
    private String nullOrValue(String value) {
        return (value != null && !value.isBlank()) ? value : "Not provided";
    }

    /**
     * Escapes HTML special characters to prevent XSS in the generated resume.
     *
     * @param input raw string that may contain HTML special chars
     * @return      safely escaped string
     */
    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
