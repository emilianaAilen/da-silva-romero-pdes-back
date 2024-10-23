package pdes.unq.com.APC.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import pdes.unq.com.APC.dtos.mercadoLibre.Category;
import pdes.unq.com.APC.interfaces.products.ProductsResponse;
import pdes.unq.com.APC.services.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        List<Category> res = productService.getCategories();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getProducts(@RequestParam("query") @Valid @NotEmpty @Size(max = 20) String queryParam) {
        List<ProductsResponse> res = productService.getProducts(queryParam);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
