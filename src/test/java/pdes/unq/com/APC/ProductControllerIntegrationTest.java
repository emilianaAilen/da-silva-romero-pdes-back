package pdes.unq.com.APC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;

import pdes.unq.com.APC.dtos.mercadoLibre.Category;
import pdes.unq.com.APC.dtos.mercadoLibre.SearchProductsResponse.Result;
import pdes.unq.com.APC.entities.Product;
import pdes.unq.com.APC.entities.ProductFavorite;
import pdes.unq.com.APC.entities.ProductPurchase;
import pdes.unq.com.APC.external_services.MercadoLibreService;
import pdes.unq.com.APC.interfaces.auth.LoginRequest;
import pdes.unq.com.APC.interfaces.products.ProductFavoriteRequest;
import pdes.unq.com.APC.interfaces.products.ProductPurchaseRequest;
import pdes.unq.com.APC.interfaces.user.UserRequest;
import pdes.unq.com.APC.repositories.ProductFavoriteRepository;
import pdes.unq.com.APC.repositories.ProductPurchaseRepository;
import pdes.unq.com.APC.repositories.ProductRepository;
import pdes.unq.com.APC.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Configuración compartida entre todos los tests
public class ProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductPurchaseRepository productPurchaseRepository;

    @Autowired
    private ProductFavoriteRepository productFavoriteRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private MercadoLibreService mercadoLibreService;

    String baseUrl = "/api/products";
    String userIDCreated;
    String token;

    @BeforeAll
    public void setupLogin() throws Exception {
        // Configuración inicial que solo se ejecuta una vez (como el login)
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("email_Test@test.com");
        userRequest.setPassword("passtest");
        userRequest.setRoleType("common");
        userRequest.setUsername("usernameTest");

        userIDCreated = userService.validateAndSaveUser(userRequest).getId();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("email_Test@test.com");
        loginRequest.setPassword("passtest");

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        token = "Bearer " + new ObjectMapper().readTree(response).get("token").asText();
    }
    
    @BeforeEach
    public void setupMocksAndData() {
        // Limpieza de datos y configuración de mocks antes de cada test
        productFavoriteRepository.deleteAll();
        productPurchaseRepository.deleteAll();
        productRepository.deleteAll();

        Product mockedProduct = new Product();
        mockedProduct.setExternalItemID("MLA123456");
        mockedProduct.setCategory("Electronics");
        mockedProduct.setName("Test Product");
        mockedProduct.setPrice(1000);

        Category mockCategory = new Category("MLA1055", "Electronics");

        Result resultMeli = new Result();
        resultMeli.setId("ML12577");
        resultMeli.setTitle("a title");
        resultMeli.setPermalink("permalink...");
        resultMeli.setThumbnail("thimbnail");
        resultMeli.setPrice(115000);
        resultMeli.setCurrency_id("ARS");
        resultMeli.setCondition("new");

        when(mercadoLibreService.getProductById(anyString())).thenReturn(mockedProduct);
        when(mercadoLibreService.getDescriptionFromProduct(anyString())).thenReturn("An description test");
        when(mercadoLibreService.getCategories()).thenReturn(List.of(mockCategory));
        when(mercadoLibreService.getProducts(anyString())).thenReturn(List.of(resultMeli));
    }

    @Test
    public void tetFavoriteProduct() throws Exception {
        ProductFavoriteRequest productFavoriteRequest = new ProductFavoriteRequest("MLA123456", userIDCreated);

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/favorite/{productId}","MLA123456")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(new Cookie("authToken", token)) 
            .content(new ObjectMapper().writeValueAsString(productFavoriteRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string("favorite product created successfully"));

        List<ProductFavorite> favorites = productFavoriteRepository.findAll();
        assertEquals(1, favorites.size());
        assertEquals("Test Product", favorites.get(0).getProduct().getName());
    }

    @Test
    public void testPurchaseProduct() throws Exception {
        ProductPurchaseRequest purchaseRequest = new ProductPurchaseRequest();
        purchaseRequest.setProductID("MLA123456");
        purchaseRequest.setUserID(userIDCreated);
        purchaseRequest.setPriceBuyed(1000);
        purchaseRequest.setCantStockBuyed(2);
        purchaseRequest.setPuntage(5);

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/purchase/{productId}","MLA12345")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(new Cookie("authToken", token))
            .content(new ObjectMapper().writeValueAsString(purchaseRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string("purchase product created successfully"));

        List<ProductPurchase> purchases = productPurchaseRepository.findAll();
        assertEquals(1, purchases.size());
        assertEquals("Test Product", purchases.get(0).getProduct().getName());
    }

    @Test
    public void TestGetCategories() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl+ "/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(new Cookie("authToken", token)))
            .andExpect(status().isOk())
            .andExpect(content().json("[{\"id\":\"MLA1055\",\"name\":\"Electronics\"}]"));
    }

    @Test
    public void TestGetProducts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl+ "/search")
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(new Cookie("authToken", token)) 
        .queryParam("query", "motorola%256"))
        .andExpect(status().isOk())
        .andExpect(content().json("[{\"id\":\"ML12577\",\"tittle\":\"a title\",\"meliLink\":\"permalink...\",\"imageLink\":\"thimbnail\",\"price\":115000,\"currency\":\"ARS\",\"condition\":\"new\"}]"));
    }

    
 }
