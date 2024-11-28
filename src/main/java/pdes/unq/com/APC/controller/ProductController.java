package pdes.unq.com.APC.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import pdes.unq.com.APC.dtos.mercadoLibre.Category;
import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.interfaces.product_purchases.ProductResponse;
import pdes.unq.com.APC.interfaces.products.ProductFavoriteRequest;
import pdes.unq.com.APC.interfaces.products.ProductFavoriteUsersResponse;
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
                    content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = Category.class))
                    )),
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
        content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProductsResponse.class))
            )),
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
    public ResponseEntity<?> AddFavoriteProduct(@PathVariable("productExternalId") String productExternalId){
        User user = getUserFromContext();
        ProductFavoriteRequest productFavoriteRequest = new ProductFavoriteRequest(productExternalId, user.getId().toString());
        
        productService.addFavoriteProduct(productFavoriteRequest);
        return new ResponseEntity<>("favorite product created successfully", HttpStatus.OK);
    }

    @Operation(summary = "Get products favorite by User id")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "List of favorite products retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found or no favorite products found", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(example = "{\"status\":\"user_not_found\",\"message\":\"No favorites found for the user\"}")
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(example = "{\"status\":\"internal_error\",\"message\":\"Unexpected error occurred\"}")
            )
        )
    })
    @GetMapping("/favorites")
    public ResponseEntity<?> GetFavoritiesProductsByUserId(){
        User user = getUserFromContext();

        List<ProductResponse> res = productService.getFavoritesProductsByUserId(user.getId().toString());

        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @Operation(summary = "Get all products favorite by User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "List of all favorite products retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProductFavoriteUsersResponse.class))
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(example = "{\"status\":\"internal_error\",\"message\":\"Unexpected error occurred\"}")
            )
        )
    })
    @GetMapping("/favorites/admin")
    public ResponseEntity<?> GetFavoritesProductsByUsers(){
        List<ProductFavoriteUsersResponse> res = productService.getAllFavoritesProductsByUsers();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    private User getUserFromContext(){
        User userDetails = null;
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            userDetails  = (User) authentication.getPrincipal();
        }
        return  userDetails;
    }

}
