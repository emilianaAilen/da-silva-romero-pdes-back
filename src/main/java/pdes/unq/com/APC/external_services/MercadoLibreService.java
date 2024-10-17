package pdes.unq.com.APC.external_services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pdes.unq.com.APC.dtos.mercadoLibre.Category;
import pdes.unq.com.APC.dtos.mercadoLibre.SearchProductsResponse;
import pdes.unq.com.APC.dtos.mercadoLibre.SearchProductsResponse.Result;
import pdes.unq.com.APC.exceptions.ExternalServiceException;
import reactor.core.publisher.Mono;

@Service
public class MercadoLibreService {

    private final WebClient webClient;
    private final String SITE_ARG = "MLA";

    @Autowired
    public MercadoLibreService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> getProductById(String productId) {
        return webClient.get()
                .uri("/items/{productId}", productId)
                .retrieve()
                .bodyToMono(String.class);
    }

    public  List<Category> getCategories(){
        List<Category> categories = webClient.get()
            .uri("/sites/" + SITE_ARG + "/categories")
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> 
                clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                    HttpStatusCode status = clientResponse.statusCode();  // Usa HttpStatusCode aquí
                    String errorMessage = String.format("External service error. Status: %s, Body: %s", status, errorBody);
                    return Mono.error(new ExternalServiceException(status, errorMessage));
                })
            )
            .bodyToFlux(Category.class)
            .collectList()
            .block();

        return categories;
    }

    public List<Result> getProducts(String query){
        String url ="/sites/" + SITE_ARG + "/search?q=" + query;

        return webClient.get()
        .uri(url)
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse ->
            clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                HttpStatusCode status = clientResponse.statusCode();
                String errorMessage = String.format("External service error. Status: %s, Body: %s", status, errorBody);
                return Mono.error(new ExternalServiceException(status, errorMessage));
            })
        )
        .bodyToMono(SearchProductsResponse.class) // Obtener la respuesta completa
        .map(searchResponse -> searchResponse.getResults()) // Extraer solo los resultados
        .block();
    }
 
}
