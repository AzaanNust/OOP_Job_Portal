package OOP.JobPortal.ResumeMatchingSystem.Services.impl;

import OOP.JobPortal.ResumeMatchingSystem.DTOs.request.ResumeRequest;
import OOP.JobPortal.ResumeMatchingSystem.DTOs.response.ResumeResponse;
import OOP.JobPortal.ResumeMatchingSystem.Entities.JobSeeker;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Resume;
import OOP.JobPortal.ResumeMatchingSystem.Exceptions.BusinessException;
import OOP.JobPortal.ResumeMatchingSystem.Exceptions.ResourceNotFoundException;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.JobSeekerRepository;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.ResumeRepository;
import OOP.JobPortal.ResumeMatchingSystem.Services.AbstractResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ============================================================
 * ResumeService  –  Resume CRUD, AI Generation, PDF Export
 * ============================================================
 *
 * WEEK 3 – Functionality (Input → Process → Output):
 *   Input:   resume data from the HTTP request
 *   Process: save to DB, call Claude AI, call PDF service
 *   Output:  ResumeResponse DTO or PDF bytes
 *
 * WEEK 3 – File Handling:
 *   getResumePdfBytes() generates a file (PDF) on demand.
 *   The Resume entity also implements Serializable (see Resume.java)
 *   enabling binary file export via Resume.saveToFile().
 *
 * WEEK 10 – Composition:
 *   This service manages the Resume that belongs to a JobSeeker.
 *   A resume cannot be created without a valid JobSeeker —
 *   enforcing the composition relationship at the service layer.
 *
 * WEEK 13 – Single Responsibility:
 *   ResumeService only manages resume data.
 *   AI generation is delegated to ClaudeAiService.
 *   PDF generation is delegated to PdfService.
 * ============================================================
 */
@Service
public class ResumeService extends AbstractResumeService {

    @Autowired
    private ResumeRepository resumeRepo;

    @Autowired
    private JobSeekerRepository seekerRepo;

    @Autowired
    private AiService aiService;

    @Autowired
    private PdfService pdfService;

    /**
     * Saves or updates the seeker's resume data.
     * If no resume exists, creates a new one.
     * If one already exists, updates it in place.
     *
     * WEEK 3 – Functionality: core "save data" operation.
     * WEEK 10 – Composition: resume is created and linked to the seeker.
     *
     * @param seekerId the ID of the job seeker
     * @param request  the resume data to save
     * @return         the saved resume as a response DTO
     * @throws ResourceNotFoundException if the seeker does not exist
     */
    @Transactional
    public ResumeResponse saveOrUpdate(Long seekerId, ResumeRequest request) {

        // Load the job seeker — throws ResourceNotFoundException if not found
        JobSeeker seeker = seekerRepo.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker", seekerId));

        // Get existing resume or create a new one
        Resume resume = resumeRepo.findByJobSeekerId(seekerId)
                .orElseGet(() -> {
                    // WEEK 10 – Composition: new resume must be linked to its seeker
                    Resume newResume = new Resume(seeker);
                    System.out.println("[ResumeService] Creating new resume for seeker: "
                            + seeker.getEmail());
                    return newResume;
                });

        // Update all fields from the request (using setters — Week 4)
        resume.setSkills(request.getSkills());
        resume.setWorkExperience(request.getWorkExperience());
        resume.setEducation(request.getEducation());
        resume.setCertifications(request.getCertifications());
        resume.setLanguages(request.getLanguages());
        resume.setPortfolioUrl(request.getPortfolioUrl());
        resume.setLinkedinUrl(request.getLinkedinUrl());
        resume.setGithubUrl(request.getGithubUrl());

        // Save to DB (WEEK 3 – file handling equivalent)
        resume = resumeRepo.save(resume);

        return ResumeResponse.from(resume);
    }

    /**
     * Calls the Claude AI API to generate a professional HTML resume.
     *
     * The seeker must have saved their resume data first.
     * The generated HTML is stored in the resume entity and used
     * by PdfService when the download endpoint is called.
     *
     * @param seekerId the ID of the job seeker
     * @return         the resume response containing the generated HTML
     * @throws ResourceNotFoundException if seeker or resume not found
     * @throws BusinessException         if no resume data has been saved
     */
    @Transactional
    public ResumeResponse generateWithAI(Long seekerId) {

        JobSeeker seeker = seekerRepo.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker", seekerId));

        // Seeker must have saved data first before AI can generate from it
        Resume resume = resumeRepo.findByJobSeekerId(seekerId)
                .orElseThrow(() -> new BusinessException(
                        "Please save your resume data first before generating with AI."));

        // Delegate to ClaudeAiService (WEEK 13 – Single Responsibility / Dependency Inversion)
        String generatedHtml = aiService.generateResumeHtml(seeker, resume);

        // Store the HTML (setAiGeneratedHtml also updates lastAiGeneratedAt)
        resume.setAiGeneratedHtml(generatedHtml);
        resume.setAiGeneratedText(stripHtmlTags(generatedHtml));

        resume = resumeRepo.save(resume);

        return ResumeResponse.from(resume);
    }

    /**
     * Returns the PDF bytes for a seeker's resume.
     * Used by ResumeController to stream a downloadable PDF.
     *
     * WEEK 3 – File Handling: generates a file (PDF) and returns the bytes.
     *
     * @param seekerId the ID of the job seeker
     * @return         PDF content as a byte array
     * @throws ResourceNotFoundException if no resume exists for this seeker
     */
    public byte[] getResumePdfBytes(Long seekerId) {

        Resume resume = resumeRepo.findByJobSeekerId(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No resume found for seeker with id: " + seekerId));

        // Delegate PDF generation to PdfService
        return pdfService.generateResumePdf(resume);
    }

    /**
     * Returns a seeker's resume data for viewing.
     *
     * @param seekerId the ID of the job seeker
     * @return         the resume response DTO
     * @throws ResourceNotFoundException if no resume exists
     */
    public ResumeResponse getBySeekerId(Long seekerId) {

        Resume resume = resumeRepo.findByJobSeekerId(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No resume found for seeker with id: " + seekerId));

        return ResumeResponse.from(resume);
    }

    /**
     * Strips HTML tags from a string to produce plain text.
     * Used to create the aiGeneratedText field for the matching algorithm.
     * WEEK 2 – String manipulation: uses replaceAll with regex.
     *
     * @param html the HTML string to strip
     * @return     plain text with all tags removed
     */
    private String stripHtmlTags(String html) {
        if (html == null) {
            return "";
        }
        // Remove all HTML tags, then collapse multiple spaces
        return html.replaceAll("<[^>]+>", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}

