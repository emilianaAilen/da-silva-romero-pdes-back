package pdes.unq.com.APC.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import pdes.unq.com.APC.dtos.mercadoLibre.Category;
import pdes.unq.com.APC.interfaces.product_purchases.ProductPurchaseRequest;
import pdes.unq.com.APC.interfaces.products.ProductCommentRequest;
import pdes.unq.com.APC.interfaces.products.ProductFavoriteRequest;
import pdes.unq.com.APC.interfaces.products.ProductsResponse;
import pdes.unq.com.APC.services.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Get all product categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved categories",
                    content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "[\n" +
                                                "    {\n" +
                                                "        \"id\": \"MLA5725\",\n" +
                                                "        \"name\": \"Electronics\"\n" +
                                                "    },\n" +
                                                "    {\n" +
                                                "        \"id\": \"MLA1055\",\n" +
                                                "        \"name\": \"Home Appliances\"\n" +
                                                "    }\n" +
                                                "]"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{\n" +
                                                "    \"status\": \"Internal_error\",\n" +
                                                "    \"message\": \"Error message\"\n" +
                                                "}")))
    })
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        List<Category> res = productService.getCategories();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Search products by query")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "[\n" +
                                                "    {\n" +
                                                "        \"id\": \"MLA1446313689\",\n" +
                                                "        \"tittle\": \"Motorola Edge 50 Fusion 5g 256 Gb Ante Rosa 8 Gb Ram\",\n" +
                                                "        \"meliLink\": \"https://www.mercadolibre.com.ar/motorola-edge-50-fusion-5g-256-gb-ante-rosa-8-gb-ram/p/MLA36687807#wid=MLA1446313689&sid=unknown\",\n" +
                                                "        \"imageLink\": \"http://http2.mlstatic.com/D_976666-MLU78414598365_082024-I.jpg\",\n" +
                                                "        \"price\": 804534,\n" +
                                                "        \"currency\": \"ARS\",\n" +
                                                "        \"condition\": \"new\"\n" +
                                                "    }\n" +
                                                "]"))),
        @ApiResponse(responseCode = "404", description = "No products found",
                    content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{\n" +
                                                "    \"status\": \"product_not_found\",\n" +
                                                "    \"message\": \"No products found for the given query\"\n" +
                                                "}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{\n" +
                                                "    \"status\": \"Internal_error\",\n" +
                                                "    \"message\": \"Error message\"\n" +
                                                "}")))
    })
    @GetMapping("/search")
    public ResponseEntity<?> getProducts(@RequestParam("query") @Valid @NotEmpty @Size(max = 20) String queryParam) {
        List<ProductsResponse> res = productService.getProducts(queryParam);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Add product to favorites")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully added product to favorites", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "\"favorite product created successfully\""))),
        @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "{\"status\":\"product_not_found\",\"message\":\"Product not found\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "{\"status\":\"Internal_error\",\"message\":\"Error message\"}")))
    })
    @PostMapping("/favorite/{productExternalId}")
    public ResponseEntity<?> AddFavoriteProduct(@PathVariable("productExternalId") String productExternalId, @RequestBody ProductFavoriteRequest productFavoriteRequest){
        productFavoriteRequest.setProductExternalId(productExternalId);
        productService.addFavoriteProduct(productFavoriteRequest);
        return new ResponseEntity<>("favorite product created successfully", HttpStatus.OK);
    }

}
