package pdes.unq.com.APC.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
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
import pdes.unq.com.APC.interfaces.product_purchases.ProductPurchaseResponse;
import pdes.unq.com.APC.interfaces.product_purchases.ProductResponse;
import pdes.unq.com.APC.interfaces.products.ProductCommentRequest;
import pdes.unq.com.APC.interfaces.products.ProductFavoriteRequest;
import pdes.unq.com.APC.interfaces.products.ProductsResponse;
import pdes.unq.com.APC.repositories.ProductCommentRepository;
import pdes.unq.com.APC.repositories.ProductFavoriteRepository;
import pdes.unq.com.APC.repositories.ProductPurchaseRepository;
import pdes.unq.com.APC.repositories.ProductRepository;

@Service
public class ProductService {

   @Autowired
   private MercadoLibreService mercadoLibreService;

   @Autowired
   private ProductPurchaseRepository productPurchaseRepository;

   @Autowired
   private ProductRepository productRepository;

   @Autowired
   private ProductCommentRepository productCommentRepository;

   @Autowired
   private ProductFavoriteRepository productFavoriteRepository;

   @Autowired
   private UserService userService;


   public List<Category> getCategories(){
      //ToDo: agregar logs, metricas a futuro.
      return  mercadoLibreService.getCategories();
   }

    public List<ProductsResponse> getProducts(String query){
       List<Result> meliProducts =  mercadoLibreService.getProducts(query);

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

    // Metodo que se encarga de guardar en neustra base de datos una compra de un usuario, guardando la informacion necesaaria
    public void purchaseProduct(ProductPurchaseRequest productPurchaseRequest){
      Product productReturn = getProductByIDAndSaveProduct(productPurchaseRequest.getProductID());

      User user = userService.getUserById(productPurchaseRequest.getUserID());

      ProductPurchase purchaseToSave  = new ProductPurchase();
      purchaseToSave.setPriceBuyed(productPurchaseRequest.priceBuyed);
      purchaseToSave.setPuntage(productPurchaseRequest.getPuntage());
      purchaseToSave.setTotalBuyed(productPurchaseRequest.getCantStockBuyed());
      purchaseToSave.setProduct(productReturn);
      purchaseToSave.setUser(user);

      try {
         productPurchaseRepository.save(purchaseToSave);
      }catch(DataAccessException e){
         System.out.println("creando PurchaseProduct Error: " + e.getMessage());
         throw new RuntimeException("Error al guardar el purchaseProduct: " + e.getMessage(), e);
      }
    }

    public Product saveProduct(Product productToSave){
         try {
            return productRepository.save(productToSave);
        } catch (DataAccessException e) {
            System.out.println("Error saving product " + e.getMessage());
            throw new RuntimeException("Error saving product: " + e.getMessage(), e);
        }
    }

   public void commentProduct(ProductCommentRequest productCommentRequest){
      ProductPurchase productPurchase = getProductPurchaseById(productCommentRequest.getPurchaseProductId());

      ProductComment productComment = new ProductComment();
      productComment.setDescription(productCommentRequest.getDescription());
      productComment.setLikes(productCommentRequest.getLikes());
      productComment.setProductPurchase(productPurchase);

      try{
         productCommentRepository.save(productComment);
      } catch (DataAccessException e) {
         System.out.println("Error saving comment product: " + e.getMessage());
         throw new RuntimeException("Error saving comment product: " + e.getMessage(), e);
      }
   }

   public void addFavoriteProduct(ProductFavoriteRequest productFavoriteRequest){
      Product product = getProductByIDAndSaveProduct(productFavoriteRequest.getProductExternalId());
      
      User user = userService.getUserById(productFavoriteRequest.getUserId());

      ProductFavorite productFavorite = new ProductFavorite();
      productFavorite.setProduct(product);
      productFavorite.setUser(user);
      productFavorite.setFavorite(true);

      try {
         productFavoriteRepository.save(productFavorite);
      } catch (Exception e) {
         System.out.println("Error saving product as favorite: " + e.getMessage());
         throw new RuntimeException("Error saving product as favorite: " + e.getMessage(), e);
      }
   }
   
   public List<ProductPurchaseResponse> getProductPurchasesFromUserId(String userId){
      List<ProductPurchase> result = productPurchaseRepository.findByUserId( UUID.fromString(userId));

      return result.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
   }

   public List<ProductResponse> getFavoritesProductsByUserId(String userId){
      List<ProductFavorite> res = productFavoriteRepository.findByUserId(UUID.fromString(userId));

      return res.stream()
                  .map(this::MapProductFavoriteToProductResponse)
                  .collect(Collectors.toList());
   }

   /**
    * Este metodo se encarga de obtener el producto de la base de datos, si no lo encuetra va por el servicio externo para traer la informacion necesaria para guardar en la BD.
    * @param externalItemId
    * @return Product
    */
    private Product getProductByIDAndSaveProduct(String externalItemId) {
      Optional<Product> productToReturn = productRepository.findByExternalItemID(externalItemId);

      if ( productToReturn.isEmpty()){
         Product product = mercadoLibreService.getProductById(externalItemId);
         String productDescription = mercadoLibreService.getDescriptionFromProduct(externalItemId);
         product.setDescription(productDescription);

         return saveProduct(product);
      }
      return productToReturn.get();
   }

   private  ProductPurchase getProductPurchaseById( String productPurchaseId){
        try {
            UUID uuid = UUID.fromString(productPurchaseId);
            System.out.println("UUID" + productPurchaseId);
            Optional<ProductPurchase> productPurchaseToReturn = productPurchaseRepository.findById(uuid);
            if (productPurchaseToReturn.isEmpty()) {
                throw new ProductPurchaseNotFoundException("ProductPurchase with id " + productPurchaseId + " not found.");
            }
            return productPurchaseToReturn.get();
        } catch (DataAccessException e) {
            System.out.println("getting ProductPurchase by id Error: " + e.getMessage());
            throw new RuntimeException("Error getting ProductPurchase by id: " + e.getMessage(), e);
        }
    }

    /**
     * Metodo que se encarga de mapear un ProducPurchase ( traido de la DB) a ProductPurchaseResponse con valores a devolver.
     * @param productPurchase
     * @return ProductPurchaseResponse 
     */
    private ProductPurchaseResponse mapToResponse(ProductPurchase productPurchase) {
      ProductPurchaseResponse result = new ProductPurchaseResponse();
      Product productFromDB = productPurchase.getProduct();

      result.setId(productPurchase.getId().toString());
      result.setUser_id(productPurchase.getUser().getId().toString());
      result.setPrice_buyed(productPurchase.getPriceBuyed());
      result.setPuntage(productPurchase.getPuntage());
      result.setTotal_buyed(productPurchase.getTotalBuyed());
      result.setProduct(mapProductToProductResponse(productFromDB));

      return result;
  }

  /**
   * Metodo para mapear un ProductFavorite a un objeto de forma ProductResponse.
   * @param productFavorite
   * @return ProductResponse
   */
  private ProductResponse MapProductFavoriteToProductResponse(ProductFavorite productFavorite){
   Product product = productFavorite.getProduct();

   return mapProductToProductResponse(product);
  }

  /**
   * Metodo que mapea un Product entity de la base de datos a un objeto de forma ProductReponse
   * @param product
   * @return ProductReponse
   */
  private ProductResponse mapProductToProductResponse(Product product){
   ProductResponse result = new ProductResponse();
   String priceStr =   String.valueOf(product.getPrice());

   result.setCategory(product.getCategory());
   result.setDescription(product.getDescription());
   result.setExternal_item_id(product.getExternalItemID());
   result.setName(product.getName());
   result.setPrice(priceStr);
   result.setId(product.getId().toString());

   return result;
  }
}
