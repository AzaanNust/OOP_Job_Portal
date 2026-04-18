package OOP.JobPortal.ResumeMatchingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ResumeMatchingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumeMatchingSystemApplication.class, args);
	}

}
