package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.Entities.Notification;
import OOP.JobPortal.ResumeMatchingSystem.Enums.AppStage;

import java.util.List;

public abstract class AbstractNotificationService {

    public abstract void notifyApplicationStageChange(String seekerEmail, String seekerName,
                                                      String jobTitle, AppStage newStage);

    public abstract void notifyApplicationReceived(String seekerEmail, String seekerName,
                                                   String jobTitle);

//    /**
//     * Notifies an employer that a new applicant has applied to their job.
//     *
//     * @param employerEmail the employer's email address
//     * @param companyName   the company name for personalisation
//     * @param seekerName    the name of the applicant
//     * @param jobTitle      the job title they applied to
//     */
//    public abstract void notifyNewApplication(String employerEmail, String companyName,
//                                              String seekerName, String jobTitle);

    public abstract List<Notification> getNotificationsForUser(String email);

    public abstract List<Notification> getUnreadNotifications(String email);

    public abstract long countUnread(String email);

    public abstract void markAsRead(Long notificationId);

    public abstract void markAllAsRead(String email);
}