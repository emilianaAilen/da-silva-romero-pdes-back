package pdes.unq.com.APC.external_services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import pdes.unq.com.APC.dtos.mercadoLibre.Category;
import pdes.unq.com.APC.dtos.mercadoLibre.DescriptionItem;
import pdes.unq.com.APC.dtos.mercadoLibre.Item;
import pdes.unq.com.APC.dtos.mercadoLibre.SearchProductsResponse;
import pdes.unq.com.APC.dtos.mercadoLibre.SearchProductsResponse.Result;
import pdes.unq.com.APC.entities.Product;
import pdes.unq.com.APC.exceptions.ExternalServiceException;
import reactor.core.publisher.Mono;

@Service
public class MercadoLibreService {

    private final WebClient webClient;
    private final MercadoLibreTokenService tokenService;
    private final String SITE_ARG = "MLA";


    private final MeterRegistry meterRegistry;
    private final String meliBasicMetric = "mercadolibre";
    private final String getProductByIdMetric = "mercadolibre.getProductById.calls";
    private final String getCategoriesMetric = "mercadolibre.getCategories.calls";
    private final String getProductsMetric = "mercadolibre.getProducts.calls";
    private final String getDescriptionFromProductMetric = "mercadolibre.getDescriptionFromProduct.calls";


    @Autowired
    public MercadoLibreService(WebClient webClient,MercadoLibreTokenService tokenService,MeterRegistry meterRegistry) {
        this.webClient = webClient;
        this.tokenService = tokenService;
        this.meterRegistry = meterRegistry; 
    }

    public Product getProductById(String productId) {
        String url = "/items/"+ productId;

        return Timer.builder(meliBasicMetric)
            .description("Tiempo de respuesta de getProductById")
            .tags("method", "getProductById")
            .register(this.meterRegistry)
            .record(() -> {
                Product productResponse = webClient.get()
                    .uri(url)
                    .headers(headers -> {
                        headers.setBearerAuth(tokenService.getAccessToken());
                    })
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                    clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                        HttpStatusCode status = clientResponse.statusCode();

                        meterRegistry.counter(getProductByIdMetric, "status", "error", "errorCode", String.valueOf(status.value()))
                            .increment();

                        if(status.equals(401)){
                            tokenService.refreshToken();
                        }
                        String errorMessage = String.format("External service error. Status: %s, Body: %s", status, errorBody);
                        return Mono.error(new ExternalServiceException(status, errorMessage));
                    }))
                    .bodyToMono(Item.class) // Obtener la respuesta completa
                    .map(item -> {
                        Product product = new Product();
                        product.setCategory(item.getCategory_id());
                        product.setName(item.getTitle());
                        product.setPrice(item.getPrice());
                        product.setExternalItemID(productId);
                        product.setUrl(item.getThumbnail());

                        return product;
                    }) 
                    .block();

                    meterRegistry.counter(getProductByIdMetric,"status", "success")
                        .increment();

                    return productResponse;
            });
    }

    public  List<Category> getCategories(){
        return Timer.builder(meliBasicMetric)
            .description("Tiempo de respuesta de getCategories")
            .tags("method", "getCategories")
            .register(this.meterRegistry)
            .record(() -> {
                List<Category> categories = webClient.get()
                    .uri("/sites/" + SITE_ARG + "/categories")
                    .headers(headers -> {
                        headers.setBearerAuth( "Bearer " + tokenService.getAccessToken());
                    })
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> 
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            HttpStatusCode status = clientResponse.statusCode();  // Usa HttpStatusCode aquí
                            meterRegistry.counter(getCategoriesMetric, "status", "error", "errorCode", String.valueOf(status.value()))
                                .increment();

                            if(status.equals(401)){
                                tokenService.refreshToken();
                            }
                            String errorMessage = String.format("External service error. Status: %s, Body: %s", status, errorBody);
                            return Mono.error(new ExternalServiceException(status, errorMessage));
                        })
                    )
                    .bodyToFlux(Category.class)
                    .collectList()
                    .block();

                meterRegistry.counter(getCategoriesMetric,"status", "success")
                    .increment();
                return categories;
            });
    }

    public List<Result> getProducts(String query){
        return Timer.builder(meliBasicMetric)
            .description("Tiempo de respuesta de getProducts")
            .tags("method", "getProducts")
            .register(this.meterRegistry)
            .record(() -> {
                String url ="/sites/" + SITE_ARG + "/search?q=" + query;

                List<Result>  results =  webClient.get()
                .uri(url)
                .headers(headers -> {
                    headers.setBearerAuth( "Bearer " + tokenService.getAccessToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                    clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                        HttpStatusCode status = clientResponse.statusCode();
                        meterRegistry.counter(getProductsMetric, "status", "error", "errorCode", String.valueOf(status.value()))
                            .increment();
        
                        if(status.equals(401)){
                            tokenService.refreshToken();
                        }
                        String errorMessage = String.format("External service error. Status: %s, Body: %s", status, errorBody);
                        return Mono.error(new ExternalServiceException(status, errorMessage));
                    })
                )
                .bodyToMono(SearchProductsResponse.class) // Obtener la respuesta completa
                .map(searchResponse -> searchResponse.getResults()) // Extraer solo los resultados
                .block();
        
                meterRegistry.counter(getProductsMetric,"status", "success")
                    .increment();

                return results;
            });
    }

    public String getDescriptionFromProduct(String itemID){
        return Timer.builder(meliBasicMetric)
            .description("Tiempo de respuesta de getDescriptionFromProduct")
            .tags("method", "getDescriptionFromProduct")
            .register(this.meterRegistry)
            .record(() -> {
                String url ="/items/"+ itemID + "/description";
                String res = webClient.get()
                    .uri(url)
                    .headers(headers -> {
                        headers.setBearerAuth( "Bearer " + tokenService.getAccessToken());
                    })
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                    clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                        HttpStatusCode status = clientResponse.statusCode();
                        meterRegistry.counter(getDescriptionFromProductMetric, "status", "error", "errorCode", String.valueOf(status.value()))
                            .increment();

                        if(status.equals(401)){
                            tokenService.refreshToken();
                        }
                        String errorMessage = String.format("External service error. Status: %s, Body: %s", status, errorBody);
                        return Mono.error(new ExternalServiceException(status, errorMessage));
                    }))
                    .bodyToMono(DescriptionItem.class) // Obtener la respuesta completa
                    .map(descriptionItem -> {
                    return descriptionItem.getPlain_text();
                    }) 
                    .block();

                meterRegistry.counter(getDescriptionFromProductMetric,"status", "success")
                    .increment();
                return res;
            });
    }
}
