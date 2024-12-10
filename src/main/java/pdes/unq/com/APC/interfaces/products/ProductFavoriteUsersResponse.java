package pdes.unq.com.APC.interfaces.products;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import pdes.unq.com.APC.interfaces.product_purchases.ProductResponse;

@Data
@AllArgsConstructor
public class ProductFavoriteUsersResponse {
    String username;
    String email;
    List<ProductResponse> productResponse;

    public ProductFavoriteUsersResponse(){}   
}
