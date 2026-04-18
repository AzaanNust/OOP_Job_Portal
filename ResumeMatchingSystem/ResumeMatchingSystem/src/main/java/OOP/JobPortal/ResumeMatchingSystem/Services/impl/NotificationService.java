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

/**
 * ============================================================
 * NotificationService  –  Email and In-App Notifications
 * ============================================================
 *
 * WEEK 7 – Polymorphism in action:
 *   This service creates both EmailNotification and InAppNotification objects,
 *   then calls send() on each. The method behaves differently at runtime
 *   depending on the actual subclass — this is dynamic method dispatch.
 *
 *   Notification n1 = new EmailNotification(...);
 *   Notification n2 = new InAppNotification(...);
 *   n1.send();  // → EmailNotification.send() — logs SMTP intent
 *   n2.send();  // → InAppNotification.send() — logs in-app storage
 *
 * WEEK 7 – Final Methods:
 *   n1.markAsRead() calls the FINAL method from Notification —
 *   no subclass can override this behaviour.
 *
 * WEEK 13 – Single Responsibility:
 *   This service only handles notification creation and delivery.
 *   It does not touch job listings, resumes, or pipeline stages.
 *
 * @Async on sendEmail(): the method runs in a background thread.
 *   A slow SMTP server won't block the API response.
 *   @EnableAsync in the main application class enables this.
 * ============================================================
 */
@Service
public class NotificationService extends AbstractNotificationService {

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Notifies a seeker that their application stage has changed.
     *
     * WEEK 7 – Dynamic Method Dispatch:
     *   We call send() on both notification types.
     *   Java resolves the correct send() implementation at runtime.
     *
     * @param seekerEmail   email of the applicant
     * @param seekerName    name of the applicant (for personalisation)
     * @param jobTitle      the job they applied for
     * @param newStage      the new pipeline stage
     */
    public void notifyApplicationStageChange(String seekerEmail, String seekerName,
                                             String jobTitle, AppStage newStage) {

        String subject = "Your application status has been updated";
        String message = buildStageChangeMessage(seekerName, jobTitle, newStage);

        // Create and save in-app notification
        InAppNotification inApp = new InAppNotification(seekerEmail, subject, message);
        inApp.send();  // WEEK 7 – polymorphic call
        notificationRepo.save(inApp);

        // Send email notification asynchronously
        sendEmail(seekerEmail, subject, message);
    }

    /**
     * Notifies a seeker that their application was received.
     *
     * @param seekerEmail  applicant's email
     * @param seekerName   applicant's name
     * @param jobTitle     job they applied for
     */
    public void notifyApplicationReceived(String seekerEmail, String seekerName,
                                          String jobTitle) {

        String subject = "Application Received – " + jobTitle;
        String message = "Hi " + seekerName + ",\n\n"
                + "Your application for \"" + jobTitle + "\" has been received.\n"
                + "We will update you as your application progresses.\n\n"
                + "Good luck!";

        InAppNotification inApp = new InAppNotification(seekerEmail, subject, message);
        inApp.send();
        notificationRepo.save(inApp);

        sendEmail(seekerEmail, subject, message);
    }

    /**
     * Retrieves all notifications for a user.
     *
     * @param email the user's email address
     * @return      all notifications, newest first
     */
    public List<Notification> getNotificationsForUser(String email) {
        return notificationRepo.findByRecipientOrderByCreatedAtDesc(email);
    }

    /**
     * Retrieves only unread notifications for a user.
     *
     * @param email the user's email
     * @return      unread notifications, newest first
     */
    public List<Notification> getUnreadNotifications(String email) {
        return notificationRepo.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(email);
    }

    /**
     * Returns the count of unread notifications (for the bell badge).
     *
     * @param email the user's email
     * @return      number of unread notifications
     */
    public long countUnread(String email) {
        return notificationRepo.countByRecipientAndIsReadFalse(email);
    }

    /**
     * Marks a single notification as read.
     * Uses the FINAL markAsRead() method from the Notification base class.
     *
     * @param notificationId the ID of the notification to mark as read
     */
    public void markAsRead(Long notificationId) {
        notificationRepo.findById(notificationId).ifPresent(notification -> {
            notification.markAsRead();  // WEEK 7 – calling a final method
            notificationRepo.save(notification);
        });
    }

    /**
     * Marks all notifications for a user as read.
     *
     * @param email the user's email
     */
    public void markAllAsRead(String email) {
        List<Notification> unread = getUnreadNotifications(email);
        for (Notification notification : unread) {
            notification.setRead(true);
        }
        notificationRepo.saveAll(unread);
    }

    /**
     * Sends an email via SMTP using Spring's JavaMailSender.
     *
     * @Async: this method runs in a background thread pool.
     *   If Gmail is slow to accept the email, the API response is not delayed.
     *
     * WEEK 12 – Exception Handling:
     *   Email delivery can fail (wrong credentials, network issue).
     *   We catch the exception and log it instead of crashing the app.
     *
     * @param to      recipient email address
     * @param subject email subject line
     * @param body    email body text
     */
    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            // Create an EmailNotification entity for auditing
            EmailNotification emailNotification = new EmailNotification(to, subject, body);
            emailNotification.send();  // WEEK 7 – polymorphic call

            // Build and send the actual email via SMTP
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);

            // Save the email record to the DB for audit trail
            notificationRepo.save(emailNotification);

        } catch (Exception e) {
            // WEEK 12 – Catch: don't let email failure crash the application
            System.err.println("[Email] Failed to send to " + to + ": " + e.getMessage());
        }
    }

    /**
     * Builds a human-readable message for a stage change notification.
     * WEEK 2 – Strings: switch expression returning descriptive message.
     *
     * @param name     the applicant's name
     * @param jobTitle the job title
     * @param stage    the new pipeline stage
     * @return         formatted notification message
     */
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

