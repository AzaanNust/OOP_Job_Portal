package OOP.JobPortal.ResumeMatchingSystem.Services.impl;

import OOP.JobPortal.ResumeMatchingSystem.Entities.EmailNotification;
import OOP.JobPortal.ResumeMatchingSystem.Entities.InAppNotification;
import OOP.JobPortal.ResumeMatchingSystem.Entities.Notification;
import OOP.JobPortal.ResumeMatchingSystem.Enums.AppStage;
import OOP.JobPortal.ResumeMatchingSystem.Repositories.NotificationRepository;
import OOP.JobPortal.ResumeMatchingSystem.Services.AbstractNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService extends AbstractNotificationService {

    @Autowired private NotificationRepository notificationRepo;
    @Autowired private JavaMailSender mailSender;

    /** Notifies a seeker that their application stage has changed. */
    public void notifyApplicationStageChange(String seekerEmail, String seekerName,
                                             String jobTitle, AppStage newStage) {
        String subject = "Your application status has been updated";
        String message = buildStageChangeMessage(seekerName, jobTitle, newStage);
        InAppNotification inApp = new InAppNotification(seekerEmail, subject, message);
        inApp.send();
        notificationRepo.save(inApp);
        sendEmail(seekerEmail, subject, message);
    }

    /** Notifies a seeker their application was received. */
    public void notifyApplicationReceived(String seekerEmail, String seekerName,
                                          String jobTitle) {
        String subject = "Application Received – " + jobTitle;
        String message = "Hi " + seekerName + ",\n\n"
                + "Your application for \"" + jobTitle + "\" has been received.\n"
                + "We will update you as your application progresses.\n\nGood luck!";
        InAppNotification inApp = new InAppNotification(seekerEmail, subject, message);
        inApp.send();
        notificationRepo.save(inApp);
        sendEmail(seekerEmail, subject, message);
    }

    /**
     * Notifies an employer that a new applicant has applied to their job.
     * Called by ApplicationService.apply() right after the application is saved.
     *
     * @param employerEmail email of the employer who posted the job
     * @param companyName   company name for personalisation
     * @param seekerName    the name of the applicant
     * @param jobTitle      the job the seeker applied to
     */
    public void notifyNewApplication(String employerEmail, String companyName,
                                     String seekerName, String jobTitle) {
        String subject = "New Application – " + jobTitle;
        String message = "Hi " + companyName + ",\n\n"
                + seekerName + " has applied for your job posting: \"" + jobTitle + "\".\n\n"
                + "Log in to the Job Portal to review their profile and resume, "
                + "and move them through the hiring pipeline.\n\n"
                + "Best regards,\nJob Portal Team";

        InAppNotification inApp = new InAppNotification(employerEmail, subject, message);
        inApp.send();
        notificationRepo.save(inApp);
        sendEmail(employerEmail, subject, message);
    }

    public List<Notification> getNotificationsForUser(String email) {
        return notificationRepo.findByRecipientOrderByCreatedAtDesc(email);
    }

    public List<Notification> getUnreadNotifications(String email) {
        return notificationRepo.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(email);
    }

    public long countUnread(String email) {
        return notificationRepo.countByRecipientAndIsReadFalse(email);
    }

    public void markAsRead(Long notificationId) {
        notificationRepo.findById(notificationId).ifPresent(notification -> {
            notification.markAsRead();
            notificationRepo.save(notification);
        });
    }

    public void markAllAsRead(String email) {
        List<Notification> unread = getUnreadNotifications(email);
        for (Notification notification : unread) {
            notification.setRead(true);
        }
        notificationRepo.saveAll(unread);
    }

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            EmailNotification emailNotification = new EmailNotification(to, subject, body);
            emailNotification.send();
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);
            notificationRepo.save(emailNotification);
        } catch (Exception e) {
            System.err.println("[Email] Failed to send to " + to + ": " + e.getMessage());
        }
    }

    private String buildStageChangeMessage(String name, String jobTitle, AppStage stage) {
        String statusDescription = switch (stage) {
            case SCREENING           -> "Your resume is being reviewed by the employer.";
            case INTERVIEW_SCHEDULED -> "Congratulations! You have been shortlisted for an interview.";
            case OFFER_SENT          -> "Great news! A job offer has been extended to you.";
            case HIRED               -> "Congratulations! You have been hired for this position!";
            case REJECTED            -> "We regret to inform you that your application was unsuccessful this time.";
            default                  -> "Your application status has been updated to: " + stage;
        };
        return "Hi " + name + ",\n\n"
                + "Regarding your application for \"" + jobTitle + "\":\n\n"
                + statusDescription + "\n\n"
                + "Please log in to the Job Portal to view the full details.\n\n"
                + "Best regards,\nJob Portal Team";
    }
}