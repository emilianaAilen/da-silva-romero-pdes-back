package pdes.unq.com.APC.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Value("${mercadolibre.api.base-url}")
    private String BASE_URL;  // URL base de la API de Mercado Libre

    @Value("${mercadolibre.api.token}")
    private String ACCESS_TOKEN;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
            .baseUrl(BASE_URL)
            .defaultHeader("Authorization", "Bearer " + ACCESS_TOKEN)  // Header para el token
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}