package OOP.JobPortal.ResumeMatchingSystem.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 * GlobalExceptionHandler  –   Exception Handling
 * ============================================================
 *
 * Exception Handling:
 *   @RestControllerAdvice is Spring's global exception handler.
 *   It intercepts ANY exception thrown anywhere in the application
 *   and converts it to a clean JSON error response.
 *
 *   Without this, Spring would return a raw HTML error page —
 *   useless for a REST API consumed by mobile/web apps.
 *
 *  try-catch equivalent:
 *   Each @ExceptionHandler method is effectively a 'catch' block
 *   for a specific exception type, applied globally across the app.
 *
 *  Single Responsibility Principle (SRP):
 *   This class has one job: converting exceptions to HTTP responses.
 *   No business logic, no database access — just error formatting.
 *
 *  Open-Closed Principle (OCP):
 *   Adding a new exception type only requires adding a new method here.
 *   Existing handlers are never modified — only extended.
 * ============================================================
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Custom Application Exceptions ─────────────────────────────

    /**
     * Handles ResourceNotFoundException → HTTP 404 Not Found.
     * Triggered when a requested database record does not exist.
     *
     * @param ex the exception containing the "not found" message
     * @return 404 response with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles BusinessException → HTTP 400 Bad Request.
     * Triggered when a business rule is violated.
     *
     * @param ex the exception with the rule violation message
     * @return 400 response with error details
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles InvalidStageTransitionException → HTTP 400 Bad Request.
     * Triggered when trying to advance an application in a terminal state.
     * Note: although checked, Spring can still intercept it here.
     *
     * @param ex the stage transition exception
     * @return 400 response
     */
    @ExceptionHandler(InvalidStageTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransition(InvalidStageTransitionException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles BadCredentialsException → HTTP 401 Unauthorized.
     * Triggered when email or password is wrong during login.
     *
     * @param ex Spring Security's bad credentials exception
     * @return 401 response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid email or password. Please try again.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handles AccessDeniedException → HTTP 403 Forbidden.
     * Triggered when a user tries to access a resource they don't have
     * permission for (e.g., a JobSeeker trying to post a job).
     *
     * @param ex the access denied exception
     * @return 403 response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "You do not have permission to perform this action.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Handles MethodArgumentNotValidException → HTTP 400 Bad Request.
     * Triggered when @Valid finds invalid input
     * (e.g., blank required field, invalid email format).
     *
     * WEEK 12 – Shows how to extract detailed field-level errors
     * and return them in a structured format.
     *
     * @param ex the validation exception containing all field errors
     * @return 400 response with a map of field → error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        // Collect all field-level validation errors into a Map
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ValidationErrorResponse error = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Input validation failed. Please check the highlighted fields.",
                fieldErrors,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Catch-all handler for any unexpected exception → HTTP 500 Internal Server Error.
     * This ensures the API never returns a raw stack trace to clients.
     *
     * @param ex any unhandled exception
     * @return 500 response with a generic message (details logged server-side)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        // Log the full stack trace server-side for debugging
        System.err.println("[ERROR] Unhandled exception: " + ex.getClass().getName()
                + " | " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again or contact support.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ================================================================
    // Inner classes for structured error responses
    // The client always receives a predictable JSON shape.
    // ================================================================

    /**
     * Standard error response body sent for most errors.
     * The client receives:
     * {
     *   "status": 404,
     *   "message": "JobListing not found with id: 99",
     *   "timestamp": "2024-04-15T10:30:00"
     * }
     */
    public static class ErrorResponse {

        private int status;
        private String message;
        private LocalDateTime timestamp;

        public ErrorResponse(int status, String message, LocalDateTime timestamp) {
            this.status    = status;
            this.message   = message;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Extended error response for validation failures.
     * Adds a fieldErrors map showing exactly which fields are invalid.
     * The client receives:
     * {
     *   "status": 400,
     *   "message": "Input validation failed...",
     *   "fieldErrors": { "email": "Must be a valid email", "fullName": "Required" },
     *   "timestamp": "2024-04-15T10:30:00"
     * }
     */
    public static class ValidationErrorResponse extends ErrorResponse {

        private Map<String, String> fieldErrors;

        public ValidationErrorResponse(int status, String message,
                                       Map<String, String> fieldErrors,
                                       LocalDateTime timestamp) {
            super(status, message, timestamp);
            this.fieldErrors = fieldErrors;
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }
    }
}