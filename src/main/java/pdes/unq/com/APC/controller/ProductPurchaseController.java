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
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import pdes.unq.com.APC.interfaces.product_purchases.ProductPurchaseRequest;
import pdes.unq.com.APC.interfaces.product_purchases.ProductPurchaseResponse;
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
        purchaseRequest.setProductID(productId);
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
        @ApiResponse(responseCode = "200", description = "comment product created successfully", 
                    content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = ProductPurchaseResponse.class))
                    )),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json", 
                                        examples = @ExampleObject(value = "{\"status\":\"Internal_error\",\"message\":\"Error message\"}")))
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProductPurchaseByUserId(@PathVariable("userId") String userId){
        List<ProductPurchaseResponse> res = productService.getProductPurchasesFromUserId(userId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
}
