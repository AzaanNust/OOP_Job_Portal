package OOP.JobPortal.ResumeMatchingSystem.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClientConfig – creates the WebClient bean for external HTTP calls.
 *
 * WebClient is Spring's non-blocking HTTP client.
 * It is used by ClaudeAiService to call the Anthropic API.
 * Without this bean, Spring cannot inject WebClient.Builder anywhere.
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates a WebClient.Builder bean that Spring will inject
     * wherever WebClient.Builder is declared as a dependency.
     *
     * @return configured WebClient.Builder
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}

