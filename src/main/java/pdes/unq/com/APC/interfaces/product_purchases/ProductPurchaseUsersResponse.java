package pdes.unq.com.APC.interfaces.product_purchases;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductPurchaseUsersResponse {
    String username;
    String email;
    List<ProductPurchaseResponse> productPurchase;

    public ProductPurchaseUsersResponse(){}
    
}
