package OOP.JobPortal.ResumeMatchingSystem.Services;

import OOP.JobPortal.ResumeMatchingSystem.entity.Notification;
import OOP.JobPortal.ResumeMatchingSystem.enums.AppStage;

import java.util.List;

public abstract class AbstractNotificationService {

    public abstract void notifyApplicationStageChange(String seekerEmail, String seekerName,
                                                      String jobTitle, AppStage newStage);

    public abstract void notifyApplicationReceived(String seekerEmail, String seekerName,
                                                   String jobTitle);

    public abstract List<Notification> getNotificationsForUser(String email);

    public abstract List<Notification> getUnreadNotifications(String email);

    public abstract long countUnread(String email);

    public abstract void markAsRead(Long notificationId);

    public abstract void markAllAsRead(String email);

    public abstract void sendEmail(String to, String subject, String body);
}