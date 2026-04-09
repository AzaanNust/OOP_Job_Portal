package OOP.JobPortal.ResumeMatchingSystem.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * ============================================================
 * InAppNotification  –  Concrete Subclass of Notification
 * ============================================================
 *
 * Polymorphism (Method Overriding):
 *   Overrides send() with a DIFFERENT behaviour than EmailNotification.
 *   Same method name, same parameters, completely different implementation.
 *   This is the power of polymorphism — the caller does not need to know
 *   which type of notification it is dealing with.
 *
 * Method Overriding vs Overloading:
 *   Overriding:  same method name + same parameters in a SUBCLASS
 *   Overloading: same method name + DIFFERENT parameters in the same class
 *   send() here is OVERRIDING (same signature, different class).
 *
 * HOW IT WORKS:
 *   When InAppNotification.send() is called, it records itself in the DB.
 *   The frontend polls GET /api/notifications every 30 seconds to fetch
 *   unread in-app notifications and shows them as a bell badge.
 * ============================================================
 */
@Entity
@DiscriminatorValue("IN_APP")
public class InAppNotification extends Notification {

    /** Default constructor required by JPA */
    public InAppNotification() {
        super();
    }

    /**
     * Constructor chaining to parent Notification.
     *
     * @param recipientEmail the target user's email
     * @param subject        notification title
     * @param message        notification body
     */
    public InAppNotification(String recipientEmail, String subject, String message) {
        super(recipientEmail, subject, message);
    }

    /**
     * Method Overriding:
     * Different from EmailNotification.send() — instead of SMTP,
     * this notification is stored in the database and displayed
     * in the app's notification panel.
     */
    @Override
    public void send() {
        System.out.println("[IN-APP] Notification for: " + getRecipient()
                + " | Message: " + getMessage());
        setSentAt(java.time.LocalDateTime.now());
        // The record is saved to DB by NotificationService.
        // Frontend fetches it via GET /api/notifications
    }

    @Override
    public String toString() {
        return "InAppNotification{recipient='" + getRecipient()
                + "', read=" + isRead() + "}";
    }
}