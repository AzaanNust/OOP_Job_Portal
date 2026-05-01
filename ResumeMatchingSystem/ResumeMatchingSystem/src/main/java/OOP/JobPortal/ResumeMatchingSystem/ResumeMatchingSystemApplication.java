package OOP.JobPortal.ResumeMatchingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * ============================================================
 * ResumeMatchingSystemApplication  –  Spring Boot Entry Point
 * ============================================================
 *
 * This is the class that starts the entire backend server.
 * Running main() launches:
 *   1. An embedded Tomcat web server on port 8080
 *   2. Hibernate ORM which creates/updates MySQL tables
 *   3. Spring Security which protects the API routes
 *   4. All @Component, @Service, @Repository beans via auto-scan
 *
 * @SpringBootApplication combines three annotations:
 *   @Configuration           → this class can define @Bean methods
 *   @EnableAutoConfiguration → Spring auto-configures itself
 *   @ComponentScan           → finds all annotated classes in this package
 *
 * @EnableAsync: enables the @Async annotation used in NotificationService.
 *   Without this, @Async methods run synchronously on the same thread.
 *   With this, they run in a background thread pool — notifications don't
 *   block the API response.
 * ============================================================
 */
@SpringBootApplication
@EnableAsync
public class ResumeMatchingSystemApplication {

	/**
	 * The main method — entry point of the Spring Boot application.
	 * SpringApplication.run() starts the embedded Tomcat server.
	 *
	 * @param args command-line arguments (not used in this application)
	 */
	public static void main(String[] args) {
		SpringApplication.run(ResumeMatchingSystemApplication.class, args);

		System.out.println("\n");
		System.out.println("=======================================================");
		System.out.println("  Job Portal Resume Matching System - STARTED");
		System.out.println("=======================================================");
		System.out.println("  Swagger UI:   http://localhost:8080/swagger-ui.html");
		System.out.println("=======================================================");
		System.out.println("  Total users created this session: "
				+ OOP.JobPortal.ResumeMatchingSystem.Entities.User.getTotalUsersCreated());
		System.out.println("=======================================================\n");
	}
}