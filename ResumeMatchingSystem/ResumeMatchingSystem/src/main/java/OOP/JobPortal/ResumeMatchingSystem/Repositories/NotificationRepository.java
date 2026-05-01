package OOP.JobPortal.ResumeMatchingSystem.Repositories;

import OOP.JobPortal.ResumeMatchingSystem.Entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Database access for the notifications table */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** All notifications for a user, newest first */
    List<Notification> findByRecipientOrderByCreatedAtDesc(String recipientEmail);

    /** Only unread notifications for a user */
    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(String recipientEmail);

    /** Count of unread notifications (used for the bell badge number) */
    long countByRecipientAndIsReadFalse(String recipientEmail);
}
