package OOP.JobPortal.ResumeMatchingSystem.Exceptions;

/**
 * ============================================================
 * InvalidStageTransitionException  –  Custom Checked Exception
 * ============================================================
 *
 * Exception Handling: Creating Custom Exceptions
 *
 * HOW TO CREATE A CUSTOM EXCEPTION:
 *   1. Extend Exception       → CHECKED exception   (caller MUST handle it)
 *   2. Extend RuntimeException → UNCHECKED exception (caller CAN ignore it)
 *
 * This is a CHECKED exception because stage transitions are
 * business-critical operations — the caller absolutely must
 * decide what to do when a transition fails.
 *
 * Checked vs Unchecked:
 *   Checked:   Must appear in throws clause or be caught (e.g., IOException)
 *   Unchecked: Optional to catch (e.g., NullPointerException, RuntimeException)
 *
 * Usage:
 *   throw new InvalidStageTransitionException("Cannot advance from HIRED");
 * ============================================================
 */
public class InvalidStageTransitionException extends Exception {

    /**
     * Creates the exception with a descriptive message explaining
     * why the stage transition is invalid.
     *
     * @param message description of what went wrong
     */
    public InvalidStageTransitionException(String message) {
        super(message);
    }

//    /**
//     * Method Overloading: second constructor with cause.
//     * Allows wrapping another exception as the cause.
//     *
//     * @param message description of what went wrong
//     * @param cause   the underlying exception that caused this
//     */
//    public InvalidStageTransitionException(String message, Throwable cause) {
//        super(message, cause);
//    }
}