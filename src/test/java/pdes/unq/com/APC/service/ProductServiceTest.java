package pdes.unq.com.APC.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pdes.unq.com.APC.dtos.mercadoLibre.Category;
import pdes.unq.com.APC.dtos.mercadoLibre.SearchProductsResponse.Result;
import pdes.unq.com.APC.entities.Product;
import pdes.unq.com.APC.entities.ProductComment;
import pdes.unq.com.APC.entities.ProductFavorite;
import pdes.unq.com.APC.entities.ProductPurchase;
import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.exceptions.ProductPurchaseNotFoundException;
import pdes.unq.com.APC.external_services.MercadoLibreService;
import pdes.unq.com.APC.interfaces.product_purchases.ProductPurchaseRequest;
import pdes.unq.com.APC.interfaces.products.ProductCommentRequest;
import pdes.unq.com.APC.interfaces.products.ProductFavoriteRequest;
import pdes.unq.com.APC.interfaces.products.ProductsResponse;
import pdes.unq.com.APC.repositories.ProductCommentRepository;
import pdes.unq.com.APC.repositories.ProductFavoriteRepository;
import pdes.unq.com.APC.repositories.ProductPurchaseRepository;
import pdes.unq.com.APC.repositories.ProductRepository;
import pdes.unq.com.APC.services.ProductService;
import pdes.unq.com.APC.services.UserService;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductPurchaseRepository productPurchaseRepository;

    @Mock
    private ProductCommentRepository productCommentRepository;

    @Mock
    private ProductFavoriteRepository productFavoriteRepository;

    @Mock
    private MercadoLibreService mercadoLibreService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProductService productService;
    
    private Product product;
    private User user;
    private ProductPurchase productPurchase;
    private ProductComment productComment;

    private ProductPurchaseRequest purchaseRequest;
    private ProductCommentRequest productCommentRequest;


    @BeforeEach
    public void setUp() {
        product = new Product();
        product.setExternalItemID("MLA12345");
        product.setName("Test Product");

        user = new User();
        user.setId(UUID.randomUUID());

        purchaseRequest = new ProductPurchaseRequest();
        purchaseRequest.setProductID("MLA12345");
        purchaseRequest.setUserID("user123");
        purchaseRequest.setPriceBuyed(100);
        purchaseRequest.setPuntage(5);
        purchaseRequest.setCantStockBuyed(1);


        UUID productPurchaseId = UUID.randomUUID();
        productCommentRequest = new ProductCommentRequest();
        productCommentRequest.setDescription("an description");
        productCommentRequest.setLikes(5);
        productCommentRequest.setPurchaseProductId(productPurchaseId.toString());

        productPurchase = new ProductPurchase();
        productPurchase.setId(productPurchaseId);
        productPurchase.setPriceBuyed(150);
        productPurchase.setProduct(product);
        productPurchase.setPuntage(10);
        productPurchase.setTotalBuyed(15);
        productPurchase.setUser(user);


        productComment = new ProductComment();
        productComment.setDescription("an description");
        productComment.setLikes(5);
        productComment.setProductPurchase(productPurchase);
    }

    @Test
    public void testGetCategories() {
        Category category = new Category("MLA1055", "Electronics");
        when(mercadoLibreService.getCategories()).thenReturn(List.of(category));

        List<Category> categories = productService.getCategories();

        assertEquals(1, categories.size());
        assertEquals("MLA1055", categories.get(0).getId());
        verify(mercadoLibreService, times(1)).getCategories();
    }

    @Test
    public void testGetProducts() {
        Result result = new Result();
        result.setId("MLA12345");
        result.setTitle("Test Product");
        when(mercadoLibreService.getProducts(anyString())).thenReturn(List.of(result));

        List<ProductsResponse> products = productService.getProducts("query");

        assertEquals(1, products.size());
        assertEquals("MLA12345", products.get(0).getId());
        verify(mercadoLibreService, times(1)).getProducts("query");
    }

    @Test
    public void testSaveProduct() {
        when(productRepository.save(product)).thenReturn(product);

        Product savedProduct = productService.saveProduct(product);

        assertNotNull(savedProduct);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    public void testPurchaseProduct() {
        when(productRepository.findByExternalItemID("MLA12345")).thenReturn(Optional.of(product));
        when(userService.getUserById("user123")).thenReturn(user);

        productService.purchaseProduct(purchaseRequest);

        verify(productPurchaseRepository, times(1)).save(any(ProductPurchase.class));
    }

    @Test
    public void testPurchaseProductGettingByMeliService() {
        when(productRepository.findByExternalItemID("MLA12345")).thenReturn(Optional.empty());
        when(mercadoLibreService.getProductById("MLA12345")).thenReturn(product);
        when(mercadoLibreService.getDescriptionFromProduct("MLA12345")).thenReturn("Product Description");
        when(productRepository.save(product)).thenReturn(product);

        // Ejecutar el método
        productService.purchaseProduct(purchaseRequest);

        verify(productRepository, times(1)).findByExternalItemID("MLA12345");
        verify(mercadoLibreService, times(1)).getProductById("MLA12345");
        verify(productRepository, times(1)).save(product);
        verify(productPurchaseRepository, times(1)).save(any(ProductPurchase.class));
    }

    @Test
    public void testProductCommentSuccefully(){
        when(productPurchaseRepository.findById(productPurchase.getId())).thenReturn(Optional.of(productPurchase));
        when(productCommentRepository.save(productComment)).thenReturn(productComment);

        productService.commentProduct(productCommentRequest);

        verify(productPurchaseRepository, times(1)).findById(productPurchase.getId());
        verify(productCommentRepository, times(1)).save(productComment);
    }

    @Test
    public void testProductCommentProductPurchaseNotFound(){
        when(productPurchaseRepository.findById(productPurchase.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductPurchaseNotFoundException.class, () -> {
            productService.commentProduct(productCommentRequest);
        });

        // Opcional: Verifica el mensaje de la excepción
        assertEquals("ProductPurchase with id " + productPurchase.getId().toString() +" not found.", exception.getMessage());

        verify(productPurchaseRepository, times(1)).findById(productPurchase.getId());
        verify(productCommentRepository, never()).save(any());
    }

    @Test
    public void testProductFavoriteSuccefully(){
        when(productRepository.findByExternalItemID("MLA12345")).thenReturn(Optional.of(product));
        when(userService.getUserById("user123")).thenReturn(user);

        ProductFavoriteRequest productFavoriteRequest = new ProductFavoriteRequest("MLA12345", "user123");
        productService.addFavoriteProduct(productFavoriteRequest);

        verify(productFavoriteRepository, times(1)).save(any(ProductFavorite.class));
    }
}
