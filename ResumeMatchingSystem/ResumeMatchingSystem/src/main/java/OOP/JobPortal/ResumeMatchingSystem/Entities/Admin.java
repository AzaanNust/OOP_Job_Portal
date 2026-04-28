//package OOP.JobPortal.ResumeMatchingSystem.Entities;
//
//import OOP.JobPortal.ResumeMatchingSystem.Enums.UserRole;
//import jakarta.persistence.*;
//
///**
// * ============================================================
// * Admin  –  Third Concrete Subclass of User
// * ============================================================
// *
// * Final Classes and Methods:
// *   Admin is declared 'final' — it cannot be subclassed further.
// *   This enforces the design decision that there is no specialised
// *   type of Admin (e.g., SuperAdmin extending Admin).
// *   The @Override getUserType() is also effectively final here
// *   because the class itself is final.
// *
// * Admin accounts cannot be created through the public API.
// * They must be inserted directly into the database with a
// * BCrypt-hashed password. This is a deliberate security decision.
// * ============================================================
// */
//@Entity
//@Table(name = "admins")
//public final class Admin extends User {
//
//    // ── Private fields ─────────────────────────────────────────────
//
//    /**
//     * Admin level describes the admin's scope of access.
//     * Examples: "SUPER_ADMIN", "MODERATOR", "ANALYST"
//     */
//    @Column(name = "admin_level")
//    private String adminLevel;
//
//    // ── Constructors ───────────────────────────────────────────────
//
//    /** Default no-arg constructor required by JPA */
//    protected Admin() {
//        super();
//    }
//
//    /**
//     * Parameterised constructor.
//     * Constructor Chaining: delegates to super().
//     *
//     * @param fullName   admin's full name
//     * @param email      login email
//     * @param password   BCrypt-hashed password
//     */
//    public Admin(String fullName, String email, String password) {
//        super(fullName, email, password, UserRole.ADMIN);
//        this.adminLevel = "MODERATOR";  // Default admin level
//    }
//
//    /**
//     * Method Overloading: constructor that specifies admin level.
//     *
//     * @param fullName   admin's full name
//     * @param email      login email
//     * @param password   BCrypt-hashed password
//     * @param adminLevel the level/type of admin access
//     */
//    public Admin(String fullName, String email, String password, String adminLevel) {
//        super(fullName, email, password, UserRole.ADMIN);
//        this.adminLevel = adminLevel;
//    }
//
//    // ── Method Overriding ──────────────────────────────────────────
//
//    /**
//     * Final Methods (implicit since class is final):
//     * Returns "Admin" as the user type identifier.
//     */
//    @Override
//    public String getUserType() {
//        return "Admin";
//    }
//
//    // ── Getters and Setters ────────────────────────────────────────
//
//    /** Returns the admin's access level */
//    public String getAdminLevel() {
//        return adminLevel;
//    }
//
//    /** Sets the admin's access level */
//    public void setAdminLevel(String adminLevel) {
//        this.adminLevel = adminLevel;
//    }
//}