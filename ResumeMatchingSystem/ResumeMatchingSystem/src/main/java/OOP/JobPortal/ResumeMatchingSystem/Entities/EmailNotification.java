package OOP.JobPortal.ResumeMatchingSystem.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * ============================================================
 * EmailNotification  –  Concrete Subclass of Notification
 * ============================================================
 *
 *  Method Overriding:
 *   Provides a concrete implementation of the abstract send() method.
 *   @Override confirms we're intentionally overriding the parent's method.
 *
 *  Polymorphism (Dynamic Method Dispatch):
 *   If you have:
 *     Notification n = new EmailNotification("a@b.com", "Hi", "Body");
 *     n.send();
 *   Java calls EmailNotification.send() at runtime — NOT Notification.send().
 *   This is the essence of runtime polymorphism.
 *
 *  Abstract vs Concrete:
 *   Notification: abstract (no direct instances)
 *   EmailNotification: concrete (can be instantiated directly)
 *
 * @DiscriminatorValue("EMAIL"):
 *   When this entity is stored in the notifications table,
 *   the notification_type column will be set to "EMAIL".
 * ============================================================
 */
@Entity
@DiscriminatorValue("EMAIL")
public class EmailNotification extends Notification {

    // ── Constructors ───────────────────────────────────────────────

    /** Default constructor required by JPA */
    public EmailNotification() {
        super();
    }

    /**
     * Parameterised constructor.
     * Constructor Chaining: calls parent constructor.
     *
     * @param recipientEmail the target email address
     * @param subject        email subject line
     * @param message        email body text
     */
    public EmailNotification(String recipientEmail, String subject, String message) {
        super(recipientEmail, subject, message);
    }

    // ================================================================
    //  Method Overriding:
    //   This overrides the abstract send() from Notification.
    //   The actual SMTP delivery is handled by NotificationService
    //   using Spring's JavaMailSender. Here we record when it was sent.
    // ================================================================

    /**
     * Sends this notification as an email.
     * Overrides the abstract send() method from Notification.
     *
     * Note: this method records the send attempt.
     * The actual SMTP delivery is done by NotificationService.sendEmail()
     * using Spring's JavaMailSender, which is not accessible here.
     */
    @Override
    public void send() {
        System.out.println("[EMAIL] Sending to: " + getRecipient()
                + " | Subject: " + getSubject());
        // Record when the send was triggered
        setSentAt(java.time.LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "EmailNotification{recipient='" + getRecipient()
                + "', subject='" + getSubject() + "'}";
    }
}