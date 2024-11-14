package pdes.unq.com.APC.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pdes.unq.com.APC.dtos.mercadoLibre.Category;
import pdes.unq.com.APC.dtos.mercadoLibre.SearchProductsResponse.Result;
import pdes.unq.com.APC.entities.Product;
import pdes.unq.com.APC.entities.ProductPurchase;
import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.external_services.MercadoLibreService;
import pdes.unq.com.APC.interfaces.products.ProductPurchaseRequest;
import pdes.unq.com.APC.interfaces.products.ProductsResponse;
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
            System.out.println("creando Product Error: " + e.getMessage());
            throw new RuntimeException("Error al guardar el product: " + e.getMessage(), e);
        }
    }

    //Este metodo se encarga de obtener el producto de la base de datos, si no lo encuetra va por el servicio externo para traer la informacion necesaria para guardar en la BD.
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
}
