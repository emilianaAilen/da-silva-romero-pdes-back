package pdes.unq.com.APC.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pdes.unq.com.APC.dtos.mercadoLibre.Category;
import pdes.unq.com.APC.dtos.mercadoLibre.SearchProductsResponse.Result;
import pdes.unq.com.APC.external_services.MercadoLibreService;
import pdes.unq.com.APC.interfaces.products.ProductsResponse;

@Service
public class ProductService {

      @Autowired
    private MercadoLibreService mercadoLibreService;

    public List<Category> getCategories(){
        //ToDo: agregar logs, metricas a futuro.
       return  mercadoLibreService.getCategories();
    }

    public List<ProductsResponse> getProductsByCategory(String category){
       List<Result> meliProducts =  mercadoLibreService.getProductsByCategories(category);

       return meliProducts.stream().map(result -> new ProductsResponse(
            result.getId(),
            result.getTitle(), 
            result.getPermalink(),
            result.getThumbnail(), 
            result.getPrice(),
            result.getCurrency_id(),
            result.getCondition()))
       .collect(Collectors.toList());


    }
    
}
