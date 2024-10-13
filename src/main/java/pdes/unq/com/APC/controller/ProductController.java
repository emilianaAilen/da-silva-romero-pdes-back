package pdes.unq.com.APC.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/search/{category}")
    public ResponseEntity<?> GetProductsByCategories(@PathVariable String category) {
        List<ProductsResponse> res = productService.getProductsByCategory(category);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
