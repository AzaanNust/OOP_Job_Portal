package OOP.JobPortal.ResumeMatchingSystem.Exceptions;

/**
 * ResourceNotFoundException  –  Unchecked Exception (extends RuntimeException)
 *
 *  Unchecked Exception:
 *   Extends RuntimeException so callers do NOT need to declare it
 *   in a throws clause. Spring's @ExceptionHandler will catch it.
 *
 * Thrown when a database record is not found.
 * Example: GET /api/jobs/999 when job 999 does not exist in the database.
 * GlobalExceptionHandler maps this to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates exception with a descriptive message.
     *
     * @param message description, e.g., "JobListing not found with id: 99"
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Method Overloading: convenience constructor.
     * Builds a standard "resource not found" message automatically.
     *
     * @param resourceName name of the entity (e.g., "JobListing")
     * @param id           the ID that was not found
     */
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }
}