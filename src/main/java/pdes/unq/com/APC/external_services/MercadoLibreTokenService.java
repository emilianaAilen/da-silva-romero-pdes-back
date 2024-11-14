package pdes.unq.com.APC.external_services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import pdes.unq.com.APC.dtos.mercadoLibre.TokenResponse;

@Service
public class MercadoLibreTokenService {

    @Value("${mercadolibre.api.base-url}")
    private String BASE_URL;  // URL base de la API de Mercado Libre

    @Value("${mercadolibre.api.token}")
    private String ACCESS_TOKEN;

    @Value("${mercadolibre.api.client_id}")
    private String CLIENT_ID;

    @Value("${mercadolibre.api.client_secret}")
    private String CLIENT_SECRET;

    // Método para obtener el token de acceso actual
    public String getAccessToken() {
        return ACCESS_TOKEN;
    }

    // Método para refrescar el token si expira
    public void refreshToken() {
       System.err.println("Refresh Token");
        WebClient webClient = WebClient.create(BASE_URL);
        
        ACCESS_TOKEN =  webClient.post()
            .uri("/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                           .with("client_id", CLIENT_ID)
                           .with("client_secret", CLIENT_SECRET)
                           .with("refresh_token", ACCESS_TOKEN))
            .retrieve()
            .bodyToMono(TokenResponse.class)
            .block()
            .getAccessToken();
    }
}
