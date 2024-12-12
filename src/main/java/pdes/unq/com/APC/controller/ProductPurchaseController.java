package pdes.unq.com.APC.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.interfaces.product_purchases.ProductPurchaseRequest;
import pdes.unq.com.APC.interfaces.product_purchases.ProductPurchaseResponse;
import pdes.unq.com.APC.interfaces.product_purchases.ProductPurchaseUsersResponse;
import pdes.unq.com.APC.interfaces.product_purchases.ProductResponse;
import pdes.unq.com.APC.interfaces.products.ProductCommentRequest;
import pdes.unq.com.APC.services.ProductService;

@RestController
@RequestMapping("/api/purchase")
public class ProductPurchaseController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Purchase a product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "purchase product created successfully", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "\"purchase product created successfully\""))),
        @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "{\"status\":\"product_Purchase_not_found\",\"message\":\"Product Purchase not found\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "{\"status\":\"Internal_error\",\"message\":\"Error message\"}")))
    })
    @PostMapping("/{productId}")
    public ResponseEntity<?> purchaseProduct(@PathVariable("productId") String productId, @RequestBody ProductPurchaseRequest purchaseRequest){
        User user = getUserFromContext();

        purchaseRequest.setProductID(productId);
        purchaseRequest.setUserID(user.getId().toString());
        productService.purchaseProduct(purchaseRequest);
        return new ResponseEntity<>("purchase product created successfully", HttpStatus.OK);
    }

    @Operation(summary = "Comment on a purchased product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "comment product created successfully", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "\"comment product created successfully\""))),
        @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "{\"status\":\"product_not_found\",\"message\":\"Product not found\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "{\"status\":\"Internal_error\",\"message\":\"Error message\"}")))
    })
    @PostMapping("/comment/{purchaseId}")
    public ResponseEntity<?> commentProduct(@PathVariable("purchaseId") String purchaseId, @RequestBody ProductCommentRequest purchaseRequest){
        purchaseRequest.setPurchaseProductId(purchaseId);
        productService.commentProduct(purchaseRequest);
        return new ResponseEntity<>("comment product created successfully", HttpStatus.OK);
    }

    @Operation(summary = "Get products purchases by user id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "get products purchases by user id successfully", 
                    content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = ProductPurchaseResponse.class))
                    )),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "{\"status\":\"Internal_error\",\"message\":\"Error message\"}")))
    })
    @GetMapping("")
    public ResponseEntity<?> getProductPurchaseByUserId(){
        User user = getUserFromContext();
        List<ProductPurchaseResponse> res = productService.getProductPurchasesFromUserId(user.getId().toString());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @Operation(summary = "Get all products purchases by users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "get alls purchases products by users successfully", 
                    content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = ProductPurchaseUsersResponse.class))
                    )),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "{\"status\":\"Internal_error\",\"message\":\"Error message\"}")))
    })
    @GetMapping("/admin")
    public ResponseEntity<?> getAllProductPurchases(){
        List<ProductPurchaseUsersResponse> res = productService.getAllPurchasesPorductsByUsers();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }    

    @Operation(summary = "Get Tops Purchases Products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "get top purchases products", 
                    content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))
                    )),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "{\"status\":\"Internal_error\",\"message\":\"Error message\"}")))
    })
    @GetMapping("/admin/top")
    public ResponseEntity<?> getTopPurchasesProducts(@RequestParam(value = "limit", defaultValue = "10") int limit){
        List<ProductResponse> res = productService.getTopPurchasesProducts(limit);
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
