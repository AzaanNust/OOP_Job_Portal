package OOP.JobPortal.ResumeMatchingSystem.Exceptions;

/**
 * BusinessException  –  Unchecked Exception for business rule violations
 *
 * Exception Handling:
 *   Thrown when business rules are violated:
 *   - Applying to the same job twice
 *   - Trying to close a job you don't own
 *   - Registering with an email already in use
 *   GlobalExceptionHandler maps this to HTTP 400 Bad Request.
 */
public class BusinessException extends RuntimeException {

    /**
     * Creates the exception with a message describing the rule violation.
     *
     * @param message description of which business rule was violated
     */
    public BusinessException(String message) {
        super(message);
    }
}