package OOP.JobPortal.ResumeMatchingSystem.Entities;

import OOP.JobPortal.ResumeMatchingSystem.Enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * ============================================================
 * User  –  Abstract Base Class
 * ============================================================
 *
 *  Inheritance:
 *   This is the superclass of JobSeeker, Employer, and Admin.
 *   It is declared abstract so it cannot be instantiated directly —
 *   you must use one of its concrete subclasses.
 *
 *  Polymorphism:
 *   getUserType() is abstract here and overridden in each subclass
 *   → runtime polymorphism (dynamic method dispatch).
 *
 *  Abstraction:
 *   Implements UserDetails (an interface from Spring Security).
 *   This demonstrates interface implementation and the difference
 *   between abstract classes and interfaces:
 *     - Abstract class: User (has some implemented methods + abstract ones)
 *     - Interface: UserDetails (all method signatures, no implementation)
 *
 *  SOLID Principles:
 *   Single Responsibility: User only handles identity and authentication.
 *   Each subclass handles its own domain-specific behaviour.
 *
 * @MappedSuperclass tells JPA: do NOT create a 'users' table.
 *   Instead, copy these fields into each subclass table.
 *   This maps directly to the inheritance relationship in our UML.
 * ============================================================
 */
@MappedSuperclass                    // JPA: share columns with subclass tables
public abstract class User implements UserDetails {

    // ================================================================
    //  Encapsulation & Data Hiding
    // All fields are PRIVATE — they cannot be accessed directly
    // from outside this class. Only getters and setters are public.
    // This protects internal state from being corrupted externally.
    // ================================================================

    /** Unique identifier — auto-incremented by the database */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    /** Full name of the user */
    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /** Email address — also used as the login username */
    @Email(message = "Must be a valid email address")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Password stored as a BCrypt hash.
     * NEVER stored as plain text.
     * BCrypt is a one-way hash — you cannot reverse it to get the password.
     */
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    /** The role this user has in the system */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /** Optional phone number */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Timestamp of when this account was created.
     * updatable = false means it is set once and never changed.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of the last update to this account */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Soft-delete flag.
     * Instead of permanently deleting a user from the database,
     * we set this to false. The account becomes inactive but data is preserved.
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    // ================================================================
    //  Static Class Members
    // A static field belongs to the CLASS, not to any one instance.
    // All User objects share this single counter.
    //  Access modifiers: private static (class-level, hidden)
    // ================================================================

    /** Counts how many User objects have been created during this session */
    private static int totalUsersCreated = 0;

    // ================================================================
    //  Constructors: Default (No-Argument) Constructor
    // Required by JPA — Hibernate uses reflection to create objects.
    // Protected so only JPA and subclasses can call it, not external code.
    // ================================================================

    /**
     * Default no-argument constructor.
     * Required by JPA/Hibernate for entity instantiation via reflection.
     * Protected access modifier ensures external code cannot create
     * a bare User — they must use a subclass with a meaningful constructor.
     */
    protected User() {
        // JPA requires this empty constructor
        // The @PrePersist method will set timestamps when saved to DB
    }

    // ================================================================
    //  Constructors: Parameterised Constructor
    // This is the constructor subclasses use to set up common fields.
    //  Constructors in Subclasses / Constructor Chaining:
    //   JobSeeker, Employer, Admin call super(...) to invoke this.
    // ================================================================

    /**
     * Parameterised constructor for setting required fields.
     * Called by subclasses using super(fullName, email, password, role).
     *
     * @param fullName  the user's full display name
     * @param email     login email address (must be unique)
     * @param password  already BCrypt-hashed password
     * @param role      the user's role in the system
     */
    protected User(String fullName, String email, String password, UserRole role) {
        this.fullName  = fullName;
        this.email     = email;
        this.password  = password;
        this.role      = role;
        this.isActive  = true;                     // New accounts start active
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        //  Static Members: increment the shared counter
        User.totalUsersCreated++;

        //  this Reference: 'this' refers to the current object instance
        System.out.println("[User] New user created via this(): " + this.email
                + " | Total created this session: " + User.totalUsersCreated);
    }

    // ================================================================
    //  Copy Constructor
    // Creates a new User by copying data from an existing one.
    // Useful for creating a duplicate profile or backup.
    // ================================================================

    /**
     * Copy constructor — creates a new User with the same data as 'other'.
     * Note: id and timestamps are NOT copied because the copy is a new record.
     *
     * @param other the User to copy data from
     */
    protected User(User other) {
        this.fullName    = other.fullName;
        this.email       = other.email + "_copy"; // Make email unique
        this.password    = other.password;
        this.role        = other.role;
        this.phoneNumber = other.phoneNumber;
        this.isActive    = other.isActive;
        this.createdAt   = LocalDateTime.now();   // Fresh timestamps
        this.updatedAt   = LocalDateTime.now();
        User.totalUsersCreated++;
    }

    // ================================================================
    //  Lifecycle Hooks (called automatically by JPA)
    // ================================================================

    /** Called by JPA just before inserting this record into the database */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /** Called by JPA just before updating this record in the database */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ================================================================
    //  Getters and Setters (manually written, no Lombok)
    // These provide controlled access to private fields.
    // A setter can include validation logic before accepting a value.
    // ================================================================

    /** Returns the unique database ID */
    public Long getId() {
        return id;
    }

    /** Returns the user's full name */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the user's full name with basic validation.
     * Demonstrates encapsulation: the setter enforces a rule
     * (name cannot be blank) before accepting the new value.
     *
     * @param fullName new full name (must not be blank)
     * @throws IllegalArgumentException if name is null or blank
     */
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be blank");
        }
        this.fullName = fullName;
        this.updatedAt = LocalDateTime.now();
    }

    /** Returns the email address */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address with format validation.
     *
     * @param email new email address
     * @throws IllegalArgumentException if email format is invalid
     */
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address: " + email);
        }
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    /** Returns the BCrypt-hashed password (never the plain text) */
    public String getPassword() {
        return password;
    }

    /** Sets a new (already hashed) password */
    public void setPassword(String password) {
        this.password  = password;
        this.updatedAt = LocalDateTime.now();
    }

    /** Returns the user's role */
    public UserRole getRole() {
        return role;
    }

    /** Sets the user's role */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /** Returns the optional phone number */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /** Sets the phone number */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /** Returns when this account was created */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Returns when this account was last updated */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /** Returns whether this account is active */
    public boolean isActive() {
        return isActive;
    }

    /** Activates or deactivates this account */
    public void setActive(boolean active) {
        this.isActive  = active;
        this.updatedAt = LocalDateTime.now();
    }

    // ================================================================
    // WEEK 4 – Static Members: static getter for the session counter
    // ================================================================

    /**
     * Returns the total number of User objects created in this session.
     * Static method: can be called without an instance — User.getTotalUsersCreated()
     *
     * @return count of users created since application started
     */
    public static int getTotalUsersCreated() {
        return totalUsersCreated;
    }

    // ================================================================
    //  Abstraction: Abstract Method
    // Every concrete subclass MUST implement this.
    // The abstract keyword here means: "I declare this method exists,
    // but I leave the implementation to whoever extends this class."
    //  Polymorphism / Dynamic Method Dispatch:
    //   User u = new JobSeeker(...);
    //   u.getUserType(); // calls JobSeeker's implementation at runtime
    // ================================================================

    /**
     * Returns a string describing what type of user this is.
     * Abstract — each subclass provides its own implementation.
     * This is the core of runtime polymorphism (dynamic dispatch).
     *
     * @return "JobSeeker", "Employer", or "Admin"
     */
    public abstract String getUserType();

    // ================================================================
    //  Interface Implementation (UserDetails from Spring Security)
    // Implementing an interface means we MUST provide all its methods.
    // This is different from an abstract class: interfaces define ONLY
    // method signatures (no implementation, no state).
    //  Method Overriding: these override interface default methods.
    // ================================================================

    /** Spring Security: returns the user's authority based on their role */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Prefix "ROLE_" is required by Spring Security's hasRole() check
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    /** Spring Security: the username used for authentication is the email */
    @Override
    public String getUsername() {
        return this.email;
    }

    /** Spring Security: account never expires in this system */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** Spring Security: account is locked when isActive is false */
    @Override
    public boolean isAccountNonLocked() {
        return this.isActive;
    }

    /** Spring Security: credentials never expire in this system */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** Spring Security: account is enabled when isActive is true */
    @Override
    public boolean isEnabled() {
        return this.isActive;
    }

    // ================================================================
    //  Method Overriding: toString overrides Object.toString()
    // ================================================================

    /**
     * Returns a human-readable string representation of this user.
     * Overrides the default Object.toString() method.
     * Does NOT include the password for security reasons.
     */
    @Override
    public String toString() {
        return getUserType()
                + "{"
                + "id=" + id
                + ", name='" + fullName + "'"
                + ", email='" + email + "'"
                + ", role=" + role
                + ", active=" + isActive
                + "}";
    }

    // ================================================================
    // Method Overloading
    // Two methods with the same name but different parameter lists.
    // Java chooses which to call based on what arguments are provided.
    // ================================================================

    /**
     * Checks if this user matches a given email (case-insensitive).
     *
     * @param searchEmail email to search for
     * @return true if emails match
     */
    public boolean matches(String searchEmail) {
        return this.email != null && this.email.equalsIgnoreCase(searchEmail);
    }

    /**
     * OVERLOADED VERSION: Checks if this user matches both email AND role.
     * Same method name, different parameters — that's method overloading.
     *
     * @param searchEmail email to match
     * @param searchRole  role to match
     * @return true if both email and role match
     */
    public boolean matches(String searchEmail, UserRole searchRole) {
        return this.matches(searchEmail) && this.role == searchRole;
    }
}