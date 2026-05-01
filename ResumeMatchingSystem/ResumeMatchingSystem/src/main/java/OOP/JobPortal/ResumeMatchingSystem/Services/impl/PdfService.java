package OOP.JobPortal.ResumeMatchingSystem.Services.impl;

import OOP.JobPortal.ResumeMatchingSystem.Entities.Resume;
import OOP.JobPortal.ResumeMatchingSystem.Exceptions.BusinessException;
import OOP.JobPortal.ResumeMatchingSystem.Services.AbstractPdfService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

/**
 * PdfService – generates a professional PDF resume directly from resume data.
 *
 * NO external AI API needed. The HTML template is built here with
 * the user's data and converted to PDF using iText html2pdf (already in build.gradle).
 *
 * The template is clean and professional — similar to the classic
 * black-and-white resume style with bold section headers and dividers.
 */
@Service
public class PdfService extends AbstractPdfService {

    public byte[] generateResumePdf(Resume resume) {
        String html = buildProfessionalHtml(resume);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(html, out, new ConverterProperties());
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Failed to generate PDF: " + e.getMessage());
        }
    }

    /**
     * Builds a professional HTML resume template from all resume fields.
     * This is converted directly to PDF — no AI needed.
     * Style is clean, minimal, professional (matching the provided screenshot).
     */
    private String buildProfessionalHtml(Resume resume) {
        String name    = esc(resume.getJobSeeker().getFullName());
        String email   = esc(resume.getJobSeeker().getEmail());
        String phone   = esc(resume.getJobSeeker().getPhoneNumber());
        int    expYrs  = resume.getTotalExperienceYears() != null ? resume.getTotalExperienceYears() : 0;

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        sb.append("<style>");
        sb.append("* { margin:0; padding:0; box-sizing:border-box; }");
        sb.append("body { font-family: Arial, Helvetica, sans-serif; font-size: 11px; color: #222; padding: 36px 48px; line-height: 1.5; }");

        // Header
        sb.append(".header { text-align: center; margin-bottom: 18px; padding-bottom: 14px; border-bottom: 2px solid #111; }");
        sb.append(".header h1 { font-size: 26px; font-weight: 900; letter-spacing: 2px; text-transform: uppercase; margin-bottom: 4px; }");
        sb.append(".header .title { font-size: 13px; color: #555; margin-bottom: 8px; }");
        sb.append(".contact { display: flex; justify-content: center; gap: 24px; font-size: 10px; color: #444; flex-wrap: wrap; }");
        sb.append(".contact span { display: inline; }");

        // Sections
        sb.append(".section { margin-top: 16px; }");
        sb.append(".section-title { font-size: 12px; font-weight: 900; letter-spacing: 1.5px; text-transform: uppercase; border-bottom: 1.5px solid #111; padding-bottom: 3px; margin-bottom: 10px; }");
        sb.append(".entry { margin-bottom: 10px; }");
        sb.append(".entry-header { display: flex; justify-content: space-between; margin-bottom: 2px; }");
        sb.append(".entry-title { font-weight: bold; font-size: 11px; }");
        sb.append(".entry-date { font-size: 10px; color: #555; }");
        sb.append(".entry-sub { font-size: 10px; color: #444; margin-bottom: 3px; }");
        sb.append(".entry-body { font-size: 10px; color: #333; }");

        // Skills
        sb.append(".skills-grid { display: flex; flex-wrap: wrap; gap: 4px; }");
        sb.append(".skill-tag { background: #f0f0f0; border: 1px solid #ccc; border-radius: 3px; padding: 2px 8px; font-size: 10px; }");
        sb.append(".skills-plain { font-size: 10px; color: #333; }");

        // Two columns for smaller sections
        sb.append(".two-col { display: flex; gap: 32px; }");
        sb.append(".two-col .col { flex: 1; }");

        sb.append("</style></head><body>");

        // ── HEADER ────────────────────────────────────────────────
        sb.append("<div class='header'>");
        sb.append("<h1>").append(name).append("</h1>");
        if (expYrs > 0) {
            sb.append("<div class='title'>").append(expYrs).append(" Year").append(expYrs != 1 ? "s" : "").append(" of Experience</div>");
        }
        sb.append("<div class='contact'>");
        if (phone != null && !phone.isBlank())
            sb.append("<span>&#9990; ").append(phone).append("</span>");
        sb.append("<span>&#9993; ").append(email).append("</span>");
        if (resume.getPortfolioUrl() != null && !resume.getPortfolioUrl().isBlank())
            sb.append("<span>&#127760; ").append(esc(resume.getPortfolioUrl())).append("</span>");
        if (resume.getLinkedinUrl() != null && !resume.getLinkedinUrl().isBlank())
            sb.append("<span>in ").append(esc(resume.getLinkedinUrl())).append("</span>");
        if (resume.getGithubUrl() != null && !resume.getGithubUrl().isBlank())
            sb.append("<span>&#128025; ").append(esc(resume.getGithubUrl())).append("</span>");
        sb.append("</div></div>");

        // ── SKILLS ────────────────────────────────────────────────
        if (hasContent(resume.getSkills())) {
            sb.append("<div class='section'>");
            sb.append("<div class='section-title'>Skills</div>");
            sb.append("<div class='skills-grid'>");
            for (String skill : resume.getSkills().split(",")) {
                String s = skill.trim();
                if (!s.isBlank())
                    sb.append("<span class='skill-tag'>").append(esc(s)).append("</span>");
            }
            sb.append("</div></div>");
        }

        // ── WORK EXPERIENCE ───────────────────────────────────────
        if (hasContent(resume.getWorkExperience())) {
            sb.append("<div class='section'>");
            sb.append("<div class='section-title'>Work Experience</div>");
            for (String line : resume.getWorkExperience().split("\n")) {
                String l = line.trim();
                if (!l.isBlank()) {
                    sb.append("<div class='entry'><div class='entry-body'>").append(esc(l)).append("</div></div>");
                }
            }
            sb.append("</div>");
        }

        // ── EDUCATION ─────────────────────────────────────────────
        if (hasContent(resume.getEducation())) {
            sb.append("<div class='section'>");
            sb.append("<div class='section-title'>Education</div>");
            for (String line : resume.getEducation().split("\n")) {
                String l = line.trim();
                if (!l.isBlank()) {
                    sb.append("<div class='entry'><div class='entry-body'>").append(esc(l)).append("</div></div>");
                }
            }
            sb.append("</div>");
        }

        // ── PROJECTS ──────────────────────────────────────────────
        if (hasContent(resume.getProjects())) {
            sb.append("<div class='section'>");
            sb.append("<div class='section-title'>Projects</div>");
            for (String line : resume.getProjects().split("\n")) {
                String l = line.trim();
                if (!l.isBlank()) {
                    // Format: "ProjectName | TechStack | Description | Link"
                    String[] parts = l.split("\\|");
                    sb.append("<div class='entry'>");
                    if (parts.length >= 1) {
                        sb.append("<div class='entry-title'>").append(esc(parts[0].trim())).append("</div>");
                    }
                    if (parts.length >= 2) {
                        sb.append("<div class='entry-sub'>Tech: ").append(esc(parts[1].trim())).append("</div>");
                    }
                    if (parts.length >= 3) {
                        sb.append("<div class='entry-body'>").append(esc(parts[2].trim())).append("</div>");
                    }
                    if (parts.length >= 4) {
                        sb.append("<div class='entry-sub'>Link: ").append(esc(parts[3].trim())).append("</div>");
                    }
                    sb.append("</div>");
                }
            }
            sb.append("</div>");
        }

        // ── CERTIFICATIONS ────────────────────────────────────────
        if (hasContent(resume.getCertifications())) {
            sb.append("<div class='section'>");
            sb.append("<div class='section-title'>Certifications</div>");
            for (String line : resume.getCertifications().split("\n")) {
                String l = line.trim();
                if (!l.isBlank())
                    sb.append("<div class='entry-body'>&bull; ").append(esc(l)).append("</div>");
            }
            sb.append("</div>");
        }

        // ── AWARDS ────────────────────────────────────────────────
        if (hasContent(resume.getAwards())) {
            sb.append("<div class='section'>");
            sb.append("<div class='section-title'>Awards &amp; Achievements</div>");
            for (String line : resume.getAwards().split("\n")) {
                String l = line.trim();
                if (!l.isBlank())
                    sb.append("<div class='entry-body'>&bull; ").append(esc(l)).append("</div>");
            }
            sb.append("</div>");
        }

        // ── VOLUNTEER + PUBLICATIONS side by side ─────────────────
        boolean hasVol = hasContent(resume.getVolunteerWork());
        boolean hasPub = hasContent(resume.getPublications());
        boolean hasLang = hasContent(resume.getLanguages());

        if (hasVol || hasPub || hasLang) {
            sb.append("<div class='section'><div class='two-col'>");

            if (hasVol) {
                sb.append("<div class='col'>");
                sb.append("<div class='section-title'>Volunteer Work</div>");
                for (String line : resume.getVolunteerWork().split("\n")) {
                    String l = line.trim();
                    if (!l.isBlank())
                        sb.append("<div class='entry-body'>&bull; ").append(esc(l)).append("</div>");
                }
                sb.append("</div>");
            }

            if (hasPub) {
                sb.append("<div class='col'>");
                sb.append("<div class='section-title'>Publications</div>");
                for (String line : resume.getPublications().split("\n")) {
                    String l = line.trim();
                    if (!l.isBlank())
                        sb.append("<div class='entry-body'>&bull; ").append(esc(l)).append("</div>");
                }
                sb.append("</div>");
            }

            if (hasLang && !hasVol && !hasPub) {
                sb.append("<div class='col'>");
                sb.append("<div class='section-title'>Languages</div>");
                sb.append("<div class='entry-body'>").append(esc(resume.getLanguages())).append("</div>");
                sb.append("</div>");
            }

            sb.append("</div></div>");
        }

        // ── LANGUAGES (if not shown above) ────────────────────────
        if (hasLang && (hasVol || hasPub)) {
            sb.append("<div class='section'>");
            sb.append("<div class='section-title'>Languages</div>");
            sb.append("<div class='entry-body'>").append(esc(resume.getLanguages())).append("</div>");
            sb.append("</div>");
        }

        // ── REFERENCES ────────────────────────────────────────────
        if (hasContent(resume.getReferencesText())) {
            sb.append("<div class='section'>");
            sb.append("<div class='section-title'>References</div>");
            for (String line : resume.getReferencesText().split("\n")) {
                String l = line.trim();
                if (!l.isBlank())
                    sb.append("<div class='entry-body'>").append(esc(l)).append("</div>");
            }
            sb.append("</div>");
        }

        sb.append("</body></html>");
        return sb.toString();
    }

    private boolean hasContent(String s) {
        return s != null && !s.isBlank();
    }

    private String esc(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}