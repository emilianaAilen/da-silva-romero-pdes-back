package pdes.unq.com.APC.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import pdes.unq.com.APC.external_services.MercadoLibreTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Value("${mercadolibre.api.base-url}")
    private String BASE_URL;  // URL base de la API de Mercado Libre

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
            .baseUrl(BASE_URL)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}