package OOP.JobPortal.ResumeMatchingSystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ============================================================
 * Notification  –  Abstract Class
 * ============================================================
 *
 * Abstract Classes and Methods:
 *   Notification is abstract — you cannot create a plain Notification.
 *   You must use one of its concrete subclasses.
 *   This enforces that every notification has a specific delivery method.
 *
 * Abstract vs Interface:
 *   Abstract class (Notification): HAS state (message, recipient, isRead),
 *     HAS concrete methods (markAsRead, getCreatedAt), HAS abstract method (send).
 *   Interface (UserDetails): only method signatures, no state.
 *   Rule: use abstract class when subclasses share state/behaviour.
 *         use interface when you just need a contract.
 *
 * Polymorphism / Dynamic Method Dispatch:
 *   Notification n = new EmailNotification(...);
 *   n.send();  // Calls EmailNotification.send() at runtime, not Notification.send()
 *   This is the essence of runtime polymorphism.
 *
 * Final Methods:
 *   markAsRead() is final — no subclass can override it.
 *   The "read" state is fundamental to all notifications and
 *   must behave consistently regardless of delivery type.
 *
 * InheritanceType.SINGLE_TABLE:
 *   All notification types share ONE table.
 *   A 'notification_type' discriminator column identifies the subclass.
 *   More efficient for querying than one-table-per-subclass.
 * ============================================================
 */
@Entity
@Table(name = "notifications")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "notification_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Notification {

    // ── Private fields (Encapsulation) ────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The recipient's email address */
    @Column(nullable = false)
    private String recipient;

    /** Subject line or notification title */
    private String subject;

    /** The main notification body text */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    /** Whether the user has seen this notification */
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** When this notification was actually sent */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    // ── Constructors ───────────────────────────────────────────────

    /** Default constructor required by JPA */
    protected Notification() {
        this.isRead    = false;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Parameterised constructor for subclasses.
     * WEEK 6 – Constructor Chaining: subclasses call super(recipient, subject, message).
     *
     * @param recipient the target user's email
     * @param subject   notification subject/title
     * @param message   notification body
     */
    protected Notification(String recipient, String subject, String message) {
        this();
        this.recipient = recipient;
        this.subject   = subject;
        this.message   = message;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ── Getters and Setters ────────────────────────────────────────

    /** Returns the notification ID */
    public Long getId() {
        return id;
    }

    /** Returns the recipient's email */
    public String getRecipient() {
        return recipient;
    }

    /** Sets the recipient email */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /** Returns the subject line */
    public String getSubject() {
        return subject;
    }

    /** Sets the subject line */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /** Returns the message body */
    public String getMessage() {
        return message;
    }

    /** Sets the message body */
    public void setMessage(String message) {
        this.message = message;
    }

    /** Returns whether this notification has been read */
    public boolean isRead() {
        return isRead;
    }

    /** Sets the read status */
    public void setRead(boolean read) {
        this.isRead = read;
    }

    /** Returns when this notification was created */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Returns when this notification was sent */
    public LocalDateTime getSentAt() {
        return sentAt;
    }

    /** Sets when this notification was sent */
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    // ================================================================
    // Abstract Method
    // Every concrete subclass MUST implement send().
    // The 'abstract' keyword says: "I declare this exists, but I
    // leave the how-to-send decision to each subclass."
    // ================================================================

    /**
     * Sends this notification to the recipient.
     * HOW it is sent depends on the subclass:
     *   EmailNotification → sends via SMTP
     *   InAppNotification → stores in DB for frontend polling
     *
     * Abstract method — no implementation here.
     */
    public abstract void send();

    // ================================================================
    // Final Methods:
    //   markAsRead() is declared final — cannot be overridden by subclasses.
    //   Read/unread state must work the same for all notification types.
    // ================================================================

    /**
     * Marks this notification as read.
     * Final: this behaviour must be identical for all notification types.
     * No subclass is allowed to change how "marking as read" works.
     */
    public final void markAsRead() {
        this.isRead = true;
        System.out.println("[Notification] Marked as read: #" + id + " for " + recipient);
    }

    @Override
    public String toString() {
        return "Notification{"
                + "id=" + id
                + ", type=" + getClass().getSimpleName()
                + ", recipient='" + recipient + "'"
                + ", subject='" + subject + "'"
                + ", read=" + isRead
                + "}";
    }
}